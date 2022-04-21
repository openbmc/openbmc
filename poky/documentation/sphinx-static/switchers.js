(function() {
  'use strict';

  var all_versions = {
    'dev': 'dev (3.5)',
    '3.4.1': '3.4.1',
    '3.3.4': '3.3.4',
    '3.2.4': '3.2.4',
    '3.1.14': '3.1.14',
    '3.0.4': '3.0.4',
    '2.7.4': '2.7.4',
  };

  var all_doctypes = {
      'single': 'Individual Webpages',
      'mega': "All-in-one 'Mega' Manual",
  };

  // Simple version comparision
  // Return 1 if a > b
  // Return -1 if a < b
  // Return 0 if a == b
  function ver_compare(a, b) {
    if (a == "dev") {
       return 1;
    }

    if (a === b) {
       return 0;
    }

    var a_components = a.split(".");
    var b_components = b.split(".");

    var len = Math.min(a_components.length, b_components.length);

    // loop while the components are equal
    for (var i = 0; i < len; i++) {
        // A bigger than B
        if (parseInt(a_components[i]) > parseInt(b_components[i])) {
            return 1;
        }

        // B bigger than A
        if (parseInt(a_components[i]) < parseInt(b_components[i])) {
            return -1;
        }
    }

    // If one's a prefix of the other, the longer one is greater.
    if (a_components.length > b_components.length) {
        return 1;
    }

    if (a_components.length < b_components.length) {
        return -1;
    }

    // Otherwise they are the same.
    return 0;
  }

  function build_version_select(current_series, current_version) {
    var buf = ['<select>'];

    $.each(all_versions, function(version, title) {
      var series = version.substr(0, 3);
      if (series == current_series) {
        if (version == current_version)
            buf.push('<option value="' + version + '" selected="selected">' + title + '</option>');
        else
            buf.push('<option value="' + version + '">' + title + '</option>');

        if (version != current_version)
            buf.push('<option value="' + current_version + '" selected="selected">' + current_version + '</option>');
      } else {
        buf.push('<option value="' + version + '">' + title + '</option>');
      }
    });

    buf.push('</select>');
    return buf.join('');
  }

  function build_doctype_select(current_doctype) {
    var buf = ['<select>'];

    $.each(all_doctypes, function(doctype, title) {
      if (doctype == current_doctype)
        buf.push('<option value="' + doctype + '" selected="selected">' +
                 all_doctypes[current_doctype] + '</option>');
      else
        buf.push('<option value="' + doctype + '">' + title + '</option>');
    });
    if (!(current_doctype in all_doctypes)) {
        // In case we're browsing a doctype that is not yet in all_doctypes.
        buf.push('<option value="' + current_doctype + '" selected="selected">' +
                 current_doctype + '</option>');
        all_doctypes[current_doctype] = current_doctype;
    }
    buf.push('</select>');
    return buf.join('');
  }

  function navigate_to_first_existing(urls) {
    // Navigate to the first existing URL in urls.
    var url = urls.shift();

    // Web browsers won't redirect file:// urls to file urls using ajax but
    // its useful for local testing
    if (url.startsWith("file://")) {
      window.location.href = url;
      return;
    }

    if (urls.length == 0) {
      window.location.href = url;
      return;
    }
    $.ajax({
      url: url,
      success: function() {
        window.location.href = url;
      },
      error: function() {
        navigate_to_first_existing(urls);
      }
    });
  }

  function get_docroot_url() {
    var url = window.location.href;
    var root = DOCUMENTATION_OPTIONS.URL_ROOT;

    var urlarray = url.split('/');
    // Trim off anything after '/'
    urlarray.pop();
    var depth = (root.match(/\.\.\//g) || []).length;
    for (var i = 0; i < depth; i++) {
      urlarray.pop();
    }

    return urlarray.join('/') + '/';
  }

  function on_version_switch() {
    var selected_version = $(this).children('option:selected').attr('value');
    var url = window.location.href;
    var current_version = DOCUMENTATION_OPTIONS.VERSION;
    var docroot = get_docroot_url()

    var new_versionpath = selected_version + '/';
    if (selected_version == "dev")
        new_versionpath = '';

    // dev versions have no version prefix
    if (current_version == "dev") {
        var new_url = docroot + new_versionpath + url.replace(docroot, "");
        var fallback_url = docroot + new_versionpath;
    } else {
        var new_url = url.replace('/' + current_version + '/', '/' + new_versionpath);
        var fallback_url = new_url.replace(url.replace(docroot, ""), "");
    }

    console.log(get_docroot_url())
    console.log(url + " to url " + new_url);
    console.log(url + " to fallback " + fallback_url);

    if (new_url != url) {
      navigate_to_first_existing([
        new_url,
        fallback_url,
        'https://www.yoctoproject.org/docs/',
      ]);
    }
  }

  function on_doctype_switch() {
    var selected_doctype = $(this).children('option:selected').attr('value');
    var url = window.location.href;
    if (selected_doctype == 'mega') {
      var docroot = get_docroot_url()
      var current_version = DOCUMENTATION_OPTIONS.VERSION;
      // Assume manuals before 3.2 are using old docbook mega-manual
      if (ver_compare(current_version, "3.2") < 0) {
        var new_url = docroot + "mega-manual/mega-manual.html";
      } else {
        var new_url = docroot + "singleindex.html";
      }
    } else {
      var new_url = url.replace("singleindex.html", "index.html")
    }

    if (new_url != url) {
      navigate_to_first_existing([
        new_url,
        'https://www.yoctoproject.org/docs/',
      ]);
    }
  }

  // Returns the current doctype based upon the url
  function doctype_segment_from_url(url) {
    if (url.includes("singleindex") || url.includes("mega-manual"))
      return "mega";
    return "single";
  }

  $(document).ready(function() {
    var release = DOCUMENTATION_OPTIONS.VERSION;
    var current_doctype = doctype_segment_from_url(window.location.href);
    var current_series = release.substr(0, 3);
    var version_select = build_version_select(current_series, release);

    $('.version_switcher_placeholder').html(version_select);
    $('.version_switcher_placeholder select').bind('change', on_version_switch);

    var doctype_select = build_doctype_select(current_doctype);

    $('.doctype_switcher_placeholder').html(doctype_select);
    $('.doctype_switcher_placeholder select').bind('change', on_doctype_switch);

    if (ver_compare(release, "3.1") < 0) {
      $('#outdated-warning').html('Version ' + release + ' of the project is now considered obsolete, please select and use a more recent version');
      $('#outdated-warning').css('padding', '.5em');
    } else if (release != "dev") {
      $.each(all_versions, function(version, title) {
        var series = version.substr(0, 3);
        if (series == current_series && version != release) {
          $('#outdated-warning').html('This document is for outdated version ' + release + ', you should select the latest release version in this series, ' + version + '.');
          $('#outdated-warning').css('padding', '.5em');
        }
      });
    }
  });
})();
