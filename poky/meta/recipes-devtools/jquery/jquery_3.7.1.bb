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

SRC_URI[js.sha256sum] = "78a85aca2f0b110c29e0d2b137e09f0a1fb7a8e554b499f740d6744dc8962cfe"
SRC_URI[min.sha256sum] = "fc9a93dd241f6b045cbff0481cf4e1901becd0e12fb45166a8f17f95823f0b1a"
SRC_URI[map.sha256sum] = "5e7d6d9c28b7f21006535e8875eb47e9667852a14c4624eed301c6cea19ae62b"

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
