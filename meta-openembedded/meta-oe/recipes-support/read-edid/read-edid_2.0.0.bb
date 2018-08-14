DESCRIPTION = "read-edid elucidates various very useful informations from a conforming PnP monitor"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d0bf70fa1ef81fe4741ec0e6231dadfd"

SRC_URI = "http://polypux.org/projects/read-edid/read-edid-${PV}.tar.gz \
           file://0001-configure-remove-check-for-x86.h-we-don-t-build-get-.patch"

SRC_URI[md5sum] = "586e7fa1167773b27f4e505edc93274b"
SRC_URI[sha256sum] = "246ec14ec509e09ac26fe6862b120481b2cc881e2f142ba40886d6eec15e77e8"

inherit autotools

do_compile() {
    oe_runmake parse-edid
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 parse-edid ${D}${bindir}
}

PACKAGES =+ "parse-edid"
FILES_parse-edid = "${bindir}/parse-edid"
