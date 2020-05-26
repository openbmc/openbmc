SUMMARY = "jQuery is a fast, small, and feature-rich JavaScript library"
HOMEPAGE = "https://jquery.com/"
LICENSE = "MIT"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://${WORKDIR}/${BP}.js;startline=8;endline=10;md5=cdb86f5bda90caec023592d2e768357c"

SRC_URI = "\
    https://code.jquery.com/${BP}.js;name=js \
    https://code.jquery.com/${BP}.min.js;name=min \
    https://code.jquery.com/${BP}.min.map;name=map \
    "

SRC_URI[js.sha256sum] = "5a93a88493aa32aab228bf4571c01207d3b42b0002409a454d404b4d8395bd55"
SRC_URI[min.sha256sum] = "0925e8ad7bd971391a8b1e98be8e87a6971919eb5b60c196485941c3c1df089a"
SRC_URI[map.sha256sum] = "8da74aec0fcdd7678a2663b3cc9bafbaf009e6d6929b28bb3dd95bced18206f6"

UPSTREAM_CHECK_REGEX = "jquery-(?P<pver>\d+(\.\d+)+)\.js"

inherit allarch

do_install() {
    install -d ${D}${datadir}/javascript/${BPN}/
    install -m 644 ${WORKDIR}/${BP}.js ${D}${datadir}/javascript/${BPN}/${BPN}.js
    install -m 644 ${WORKDIR}/${BP}.min.js ${D}${datadir}/javascript/${BPN}/${BPN}.min.js
    install -m 644 ${WORKDIR}/${BP}.min.map ${D}${datadir}/javascript/${BPN}/${BPN}.min.map
}

PACKAGES = "${PN}"
FILES_${PN} = "${datadir}"

BBCLASSEXTEND += "native nativesdk"
