SUMMARY = "jQuery is a fast, small, and feature-rich JavaScript library"
HOMEPAGE = "https://jquery.com/"
LICENSE = "MIT"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://${WORKDIR}/${BP}.js;startline=8;endline=10;md5=b1e67ece919e852643f1541a54492d65"

SRC_URI = "\
    https://code.jquery.com/${BP}.js;name=js \
    https://code.jquery.com/${BP}.min.js;name=min \
    https://code.jquery.com/${BP}.min.map;name=map \
    "

SRC_URI[js.sha256sum] = "aff01a147aeccc9b70a5efad1f2362fd709f3316296ec460d94aa7d31decdb37"
SRC_URI[min.sha256sum] = "c4dccdd9ae25b64078e0c73f273de94f8894d5c99e4741645ece29aeefc9c5a4"
SRC_URI[map.sha256sum] = "3149351c8cbc3fb230bbf6188617c7ffda77d9e14333f4f5f0aa1aae379df892"

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
