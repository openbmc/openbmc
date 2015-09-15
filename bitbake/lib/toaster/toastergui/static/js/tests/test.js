"use strict";
/* Unit tests for Toaster's JS */

/* libtoaster tests */

QUnit.test("Layer alert notification", function(assert) {
  var layer = {
    "layerdetailurl":"/toastergui/project/1/layer/22",
    "vcs_url":"git://example.com/example.git",
    "detail":"[ git://example.com/example.git | master ]",
    "vcs_reference":"master",
    "id": 22,
    "name":"meta-example"
  };

  var correctResponse = "You have added <strong>3</strong> layers to your project: <a id=\"layer-affected-name\" href=\"/toastergui/project/1/layer/22\">meta-example</a> and its dependencies <a href=\"/toastergui/project/1/layer/9\" data-original-title=\"\" title=\"\">meta-example-two</a>, <a href=\"/toastergui/project/1/layer/9\" data-original-title=\"\" title=\"\">meta-example-three</a>";

  var layerDepsList = [
    {
    "layerdetailurl":"/toastergui/project/1/layer/9",
    "vcs_url":"git://example.com/example.git",
    "detail":"[ git://example.com/example.git | master ]",
    "vcs_reference":"master",
    "id": 9,
    "name":"meta-example-two"
    },
    {
    "layerdetailurl":"/toastergui/project/1/layer/9",
    "vcs_url":"git://example.com/example.git",
    "detail":"[ git://example.com/example.git | master ]",
    "vcs_reference":"master",
    "id": 10,
    "name":"meta-example-three"
    },
  ];

  var msg = libtoaster.makeLayerAddRmAlertMsg(layer, layerDepsList, true);
  var test = $("<div></div>");

  test.html(msg);

  assert.equal(test.children("strong").text(), "3");
  assert.equal(test.children("a").length, 3);
});

QUnit.test("Project info", function(assert){
  var done = assert.async();
  libtoaster.getProjectInfo(libtoaster.ctx.projectPageUrl, function(prjInfo){
    assert.ok(prjInfo.machine.name);
    assert.ok(prjInfo.releases.length > 0);
    assert.ok(prjInfo.layers.length > 0);
    assert.ok(prjInfo.freqtargets);
    assert.ok(prjInfo.release);
    done();
  });
});

QUnit.test("Show notification", function(assert){
  var msg = "Testing";
  var element = $("#change-notification-msg");

  libtoaster.showChangeNotification(msg);

  assert.equal(element.text(), msg);
  assert.ok(element.is(":visible"));

  $("#change-notification").hide();
});

var layer = {
  "id": 91,
  "name":  "meta-crystalforest",
  "layerdetailurl": "/toastergui/project/4/layer/91"
};

QUnit.test("Add layer", function(assert){
  var done = assert.async();

  /* Wait for the modal to be added to the dom */
  var checkModal = setInterval(function(){
    if ($("#dependencies-modal").length > 0) {
      $("#dependencies-modal .btn-primary").click();
      clearInterval(checkModal);
    }
  }, 200);

  libtoaster.addRmLayer(layer, true, function(deps){
    assert.equal(deps.length, 1);
    done();
  });

});

QUnit.test("Rm layer", function(assert){
  var done = assert.async();

  libtoaster.addRmLayer(layer, false, function(deps){
    assert.equal(deps.length, 0);
    done();
  });

});

QUnit.test("Parse url params", function(assert){
  var params = libtoaster.parseUrlParams();
  assert.ok(params);
});

QUnit.test("Dump url params", function(assert){
  var params = libtoaster.dumpsUrlParams();
  assert.ok(params);
});

QUnit.test("Make typeaheads", function(assert){
  var layersT = $("#layers");
  var machinesT = $("#machines");
  var projectsT = $("#projects");
  var recipesT = $("#recipes");

  libtoaster.makeTypeahead(layersT,
    libtoaster.ctx.layersTypeAheadUrl, {}, function(){});

  libtoaster.makeTypeahead(machinesT,
    libtoaster.ctx.machinesTypeAheadUrl, {}, function(){});

  libtoaster.makeTypeahead(projectsT,
    libtoaster.ctx.projectsTypeAheadUrl, {}, function(){});

  libtoaster.makeTypeahead(recipesT,
    libtoaster.ctx.recipesTypeAheadUrl, {}, function(){});

  assert.ok(recipesT.data('typeahead'));
  assert.ok(layersT.data('typeahead'));
  assert.ok(projectsT.data('typeahead'));
  assert.ok(recipesT.data('typeahead'));
});



/* Page init functions */

QUnit.test("Import layer page init", function(assert){
  assert.throws(importLayerPageInit());
});

QUnit.test("Project page init", function(assert){
  assert.throws(projectPageInit());
});

QUnit.test("Layer details page init", function(assert){
  assert.throws(layerDetailsPageInit());
});

QUnit.test("Layer btns init", function(assert){
  assert.throws(layerBtnsInit({ projectLayers : [] }));
});

QUnit.test("Table init", function(assert){
  assert.throws(tableInit({ url : tableUrl }));
});

$(document).ajaxError(function(event, jqxhr, settings, errMsg){
  if (errMsg === 'abort')
    return;

  QUnit.test("Ajax error", function(assert){
    assert.notOk(jqxhr.responseText);
  });
});






