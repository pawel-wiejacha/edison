d3.json("data.json", onDataLoaded)

var treeConf = {};
treeConf.margin = {top: 60, right: 120, bottom: 20, left: 120};
treeConf.width = 1260 - treeConf.margin.right - treeConf.margin.left;
treeConf.height = 800 - treeConf.margin.top - treeConf.margin.bottom;
treeConf.plotXRange = 800
treeConf.plotYRange = 800
treeConf.plotWidth = 85;
treeConf.plotHeight = treeConf.plotWidth;
treeConf.plotMargin = 5;
treeConf.nodeWidth = 3 * treeConf.plotWidth + 2 * treeConf.plotMargin;
treeConf.nodeHeight = treeConf.plotHeight + 2 * treeConf.plotMargin;
treeConf.nodePlotSeparatorSize = 10;

var margin = treeConf.margin,
    width = treeConf.width,
    height = treeConf.height,
    nodeWidth = treeConf.nodeWidth,
    nodeHeight = treeConf.nodeHeight;

var tree = null;
var root = null;
var svg = null;

function onDataLoaded(error, treeData) {
    //console.log("onDataLoaded", error, treeData)

    tree = d3.layout.tree()
        .size(null)
        .nodeSize([nodeWidth * 1.2, nodeWidth * 1.2])
        .separation(function (a, b) { return (a.parent == b.parent ? 1 : 1.2); });

    var zoomListener = d3.behavior.zoom().scaleExtent([0.1, 8]).on("zoom", zoom);

    svg = d3.select("body").append("svg")
        .attr("width", width + margin.right + margin.left)
        .attr("height", height + margin.top + margin.bottom)
        .call(zoomListener)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")")

    function zoom() {
        var dx = d3.event.translate[0] + margin.left;
        var dy = d3.event.translate[1] + margin.top;
        svg.attr("transform", "translate(" + dx + "," + dy + ")scale(" + d3.event.scale + ")");
    }

    root = treeData[0];
    root.x0 = height / 2;
    root.y0 = 0;

    update(root);

    d3.select(self.frameElement).style("height", "700px");
}

function update(source) {
    var i = 0,
        duration = 100;

    var diagonal = d3.svg.diagonal()
        .projection(function (d) {
            return [d.x, d.y];
        });

    // Compute the new tree layout.
    var nodes = tree.nodes(root).reverse();
    var links = tree.links(nodes);

    // Normalize for fixed-depth.
    nodes.forEach(function (d) {
        d.y = d.depth * 180;
    });

    addSamplePlotParts(nodes);

    // Update the nodes
    var node = svg.selectAll("g.node")
        .data(nodes, function (d) {
            return d.id || (d.id = ++i);
        });

    // Enter any new nodes at the parent's previous position.
    var nodeEnter = node.enter().append("g")
        //.call(dragListener)
        .attr("class", "node")
        .attr("transform", function (d) { return "translate(" + d.x + "," + d.y + ")"; })
        .on("click", toggleChildren);

    nodeEnter.append("rect")
        .attr("class", "node-frame")
        .attr("width", nodeWidth)
        .attr("height", nodeHeight)
        .attr("x", function (d) { return -nodeWidth / 2; })
        .attr("y", function (d) { return -nodeHeight / 2; });

    function addTextLine(dy, clazz, textFun) {
        nodeEnter.append("text")
            .attr("x", function (d) { return -nodeWidth / 2; })
            .attr("y", function (d) { return -nodeHeight / 2; })
            .attr("class", clazz)
            .attr("dy", dy + "px")
            .attr("dx", "5px")
            .attr("text-anchor", function (d) { return "start"; })
            .text(textFun)
    }

    addTextLine(15, "header", function (d) { return d.name; })
    addTextLine(30, "normal", function (d) { return "min: " + d.min.toFixed(5); });
    addTextLine(45, "normal", function (d) { return "max: " + d.max.toFixed(5); });
    addTextLine(60, "normal", function (d) { return "mean: " + d.mean.toFixed(5); });

    addSamplePlots(nodeEnter)

    // Transition nodes to their new position.
    var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });

    // Transition exiting nodes to the parent's new position.
    var nodeExit = node.exit().transition()
        .duration(duration)
        .attr("transform", function (d) {
            return "translate(" + source.x + "," + source.y + ")";
        })
        .remove();

    nodeExit.select("text")
        .style("fill-opacity", 1e-6);

    // Update the links…
    var link = svg.selectAll("path.link")
        .data(links, function (d) {
            return d.target.id;
        });

    // Enter any new links at the parent's previous position.
    link.enter().insert("path", "g")
        .attr("class", "link")
        .attr("d", function (d) {
            var o = {x: source.x0, y: source.y0};
            return diagonal({source: o, target: o});
        });

    // Transition links to their new position.
    link.transition()
        .duration(duration)
        .attr("d", diagonal);

    // Transition exiting nodes to the parent's new position.
    link.exit().transition()
        .duration(duration)
        .attr("d", function (d) {
            var o = {x: source.x, y: source.y};
            return diagonal({source: o, target: o});
        })
        .remove();

    // Stash the old positions for transition.
    nodes.forEach(function (d) {
        d.x0 = d.x;
        d.y0 = d.y;
    });
}

function toggleChildren(d) {
    if (d.children) {
        d._children = d.children;
        d.children = null;
    } else {
        d.children = d._children;
        d._children = null;
    }
    update(d);
}

function addSamplePlotParts(nodes) {
    nodes.forEach(function (node) {
        if (node.samplePlotParts) return; // already generated

        var x = d3.scale.linear()
            .range([0, treeConf.plotXRange]);

        var y = d3.scale.linear()
            .range([treeConf.plotYRange, 0]);

        x.domain(d3.extent(node.samples, function (sample) { return sample.param; })).nice();
        y.domain(d3.extent(node.samples, function (sample) { return sample.result; })).nice();

        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom")
            .ticks(5)
            .tickSize(-treeConf.plotXRange);

        var yAxis = d3.svg.axis()
            .scale(y)
            .orient("left")
            .ticks(5)
            .tickSize(-treeConf.plotYRange);

        var samplePlotParts = {
            samples: node.samples,
            x: x,
            y: y,
            xAxis: xAxis,
            yAxis: yAxis
        }

        node.samplePlotParts = samplePlotParts;

        node.samples.forEach(function (sample) {
            sample.samplePlotParts = samplePlotParts;
        });
    });
}

function addSamplePlots(nodeEnter) {
    var translateXOffset = treeConf.nodeWidth / 2 - treeConf.plotWidth - treeConf.plotMargin;
    var translateYOffset = (-treeConf.nodeHeight / 2) + treeConf.plotMargin;
    var translateStr = "translate(" + translateXOffset + "," + translateYOffset + ")";
    var scaleY = treeConf.plotHeight / treeConf.plotYRange;
    var scaleX = scaleY; //treeConf.plotWidth / treeConf.plotXRange;
    var scaleStr = "scale(" + scaleX + "," + scaleY + ")"

    nodeEnter.append("rect")
        .attr("class", "plot-background")
        .attr("transform", translateStr + scaleStr)
        .attr("fill", "rgba(255, 0, 255, 1)")
        .attr("x", "0")
        .attr("y", "0")
        .attr("width", treeConf.plotXRange)
        .attr("height", treeConf.plotYRange)

    var nodeEnter2 = nodeEnter.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(" + translateXOffset + "," + (translateYOffset + treeConf.plotYRange * scaleY) + ")" + scaleStr);

    // FIXME nasty workaround
    nodeEnter2.each(function (d, i) { d.samplePlotParts.xAxis(nodeEnter2.filter(function (_, j) { return j == i; })); })
        .append("text")
        .attr("class", "label")
        .attr("x", translateXOffset + treeConf.plotXRange / 2)
        .attr("y", "30px")
        .style("text-anchor", "end")
        .text("CacheSize");

    var nodeEnter2 = nodeEnter.append("g")
        .attr("transform", translateStr + scaleStr)
        .attr("class", "y axis");

    nodeEnter2.each(function (d, i) { d.samplePlotParts.yAxis(nodeEnter2.filter(function (_, j) { return j == i; })); })
        .append("text")
        .attr("class", "label")
        .attr("transform", "rotate(-90)")
        .attr("x", translateYOffset - treeConf.plotYRange / 2 + 100)
        .attr("y", "-10px")
        .style("text-anchor", "end")
        .text("Performance")

    nodeEnter.selectAll(".dot")
        .data(function (d) { return d.samples; })
        .enter().append("circle")
        .attr("transform", translateStr + scaleStr)
        .attr("class", "dot")
        .attr("r", 8.5)
        .attr("cx", function (d) { return d.samplePlotParts.x(d.param); })
        .attr("cy", function (d) { return d.samplePlotParts.y(d.result); })
        .style("fill", function (d) { return "#f00" });
}
