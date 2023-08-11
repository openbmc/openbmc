SUMMARY = "jQuery is a fast, small, and feature-rich JavaScript library"
HOMEPAGE = "https://jquery.com/"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://${S}/${BP}.js;beginline=5;endline=7;md5=9c7c6e9ab275fc1e0d99cb7180ecd14c"

# unpack items to ${S} so the archiver can see them
#
SRC_URI = "\
    https://code.jquery.com/${BP}.js;name=js;subdir=${BP} \
    https://code.jquery.com/${BP}.min.js;name=min;subdir=${BP} \
    https://code.jquery.com/${BP}.min.map;name=map;subdir=${BP} \
    "

SRC_URI[js.sha256sum] = "265a924c42de4784cba8fd0e1bd77133bc833ea5f5a31fc77e08922c18fcfa43"
SRC_URI[min.sha256sum] = "d8f9afbf492e4c139e9d2bcb9ba6ef7c14921eb509fb703bc7a3f911b774eff8"
SRC_URI[map.sha256sum] = "cae47e834ee977975a48c851b165cc52ea916cc968ba7d280b1293f573cd1a48"

UPSTREAM_CHECK_REGEX = "jquery-(?P<pver>\d+(\.\d+)+)\.js"

# https://github.com/jquery/jquery/issues/3927
CVE_STATUS[CVE-2007-2379] = "upstream-wontfix: There are ways jquery can expose security issues but any issues \
are in the apps exposing them and there is little we can directly do."

inherit allarch

do_install() {
    install -d ${D}${datadir}/javascript/${BPN}/
    install -m 644 ${S}/${BP}.js ${D}${datadir}/javascript/${BPN}/${BPN}.js
    install -m 644 ${S}/${BP}.min.js ${D}${datadir}/javascript/${BPN}/${BPN}.min.js
    install -m 644 ${S}/${BP}.min.map ${D}${datadir}/javascript/${BPN}/${BPN}.min.map
}

PACKAGES = "${PN}"
FILES:${PN} = "${datadir}"

BBCLASSEXTEND += "native nativesdk"
