SUMMARY = "jQuery is a fast, small, and feature-rich JavaScript library"
HOMEPAGE = "https://jquery.com/"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://${S}/${BP}.js;beginline=8;endline=10;md5=9c7c6e9ab275fc1e0d99cb7180ecd14c"

# unpack items to ${S} so the archiver can see them
#
SRC_URI = "\
    https://code.jquery.com/${BP}.js;name=js;subdir=${BP} \
    https://code.jquery.com/${BP}.min.js;name=min;subdir=${BP} \
    https://code.jquery.com/${BP}.min.map;name=map;subdir=${BP} \
    "

SRC_URI[js.sha256sum] = "9d02ee01919145c20b03ee9d3013af7118793dedf5d2c0696a773af90066c953"
SRC_URI[min.sha256sum] = "a6f3f0faea4b3d48e03176341bef0ed3151ffbf226d4c6635f1c6039c0500575"
SRC_URI[map.sha256sum] = "156b740931ade6c1a98d99713eeb186f93847ffc56057e973becab4d037ed53a"

UPSTREAM_CHECK_REGEX = "jquery-(?P<pver>\d+(\.\d+)+)\.js"

# https://github.com/jquery/jquery/issues/3927
# There are ways jquery can expose security issues but any issues are in the apps exposing them
# and there is little we can directly do
CVE_CHECK_IGNORE += "CVE-2007-2379"

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
