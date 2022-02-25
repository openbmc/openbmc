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

SRC_URI[js.sha256sum] = "1fe2bb5390a75e5d61e72c107cab528fc3c29a837d69aab7d200e1dbb5dcd239"
SRC_URI[min.sha256sum] = "ff1523fb7389539c84c65aba19260648793bb4f5e29329d2ee8804bc37a3fe6e"
SRC_URI[map.sha256sum] = "399548fb0e7b146c12f5ba18099a47d594a970fee96212eee0ab4852f3e56782"

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
