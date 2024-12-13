require ttf.inc

SUMMARY = "WenQuanYi Zen Hei - A Hei-Ti Style Chinese font"
HOMEPAGE = "http://wenq.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf540fc7d35b5777e36051280b3a911c"

SRC_URI = "${SOURCEFORGE_MIRROR}/wqy/wqy-zenhei-${PV}.tar.gz"
SRC_URI[md5sum] = "4c6c3f4e902dd5ee0a121e8c41d040bd"
SRC_URI[sha256sum] = "e4b7e306475bf9427d1757578f0e4528930c84c44eaa3f167d4c42f110ee75d6"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/wqy/files/wqy-zenhei/"
UPSTREAM_CHECK_REGEX = "wqy-zenhei/(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/wqy-zenhei"

do_install:append () {
    sed -i -e '/<string>[^W]/d' ${S}/44-wqy-zenhei.conf
    install -d ${D}${sysconfdir}/fonts/conf.d

    for x in ${S}/*.conf; do
        install -m 0644 $x ${D}${sysconfdir}/fonts/conf.d/
    done
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/fonts ${sysconfdir}"
