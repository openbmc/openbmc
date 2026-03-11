var fs = require('fs');
var system = require('system');
var page = require('webpage').create();

// Examine console log for message from chart drawing
page.onConsoleMessage = function(msg) {
    console.log(msg);
    if (msg === "ALL CHARTS READY") {
        window.charts_ready = true;
    }
    else if (msg.slice(0, 11) === "CHART READY") {
        var chart_id = msg.split(" ")[2];
        console.log('grabbing ' + chart_id);
        var png_data = page.evaluate(function (chart_id) {
            var chart_div = document.getElementById(chart_id + '_png');
            return chart_div.outerHTML;
        }, chart_id);
        fs.write(args[2] + '/' + chart_id + '.png', png_data, 'w');
    }
};

// Check command line arguments
var args = system.args;
if (args.length != 3) {
    console.log("USAGE: " + args[0] + " REPORT_HTML OUT_DIR\n");
    phantom.exit(1);
}

// Open the web page
page.open(args[1], function(status) {
    if (status == 'fail') {
        console.log("Failed to open file '" + args[1] + "'");
        phantom.exit(1);
    }
});

// Check status every 100 ms
interval = window.setInterval(function () {
    //console.log('waiting');
    if (window.charts_ready) {
        clearTimeout(timer);
        clearInterval(interval);

        var fname = args[1].replace(/\/+$/, "").split("/").pop()
        console.log("saving " + fname);
        fs.write(args[2] + '/' + fname, page.content, 'w');
        phantom.exit(0);
    }
}, 100);

// Time-out after 10 seconds
timer = window.setTimeout(function () {
    clearInterval(interval);
    console.log("ERROR: timeout");
    phantom.exit(1);
}, 10000);
