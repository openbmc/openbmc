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

SRC_URI[js.sha256sum] = "df3941e6cdaec28533ad72b7053ec05f7172be88ecada345c42736bc2ffba4d2"
SRC_URI[min.sha256sum] = "a3cf00c109d907e543bc4f6dbc85eb31068f94515251347e9e57509b52ee3d74"
SRC_URI[map.sha256sum] = "856ee620cebac56e872d6e99b09de05f81ccd3f3dc346e9b55eb694611a6d5e1"

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
