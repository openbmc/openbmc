require ttf.inc

SUMMARY = "Liberation(tm) Fonts"
DESCRIPTION = "The Liberation(tm) Fonts is a font family originally \
created by Ascender(c) which aims at metric compatibility with \
Arial, Times New Roman, Courier New."

HOMEPAGE = "https://fedorahosted.org/liberation-fonts/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "https://releases.pagure.org/liberation-fonts/liberation-fonts-ttf-${PV}.tar.gz \
           file://30-0-liberation-sans-narrow.conf \
"

SRC_URI[sha256sum] = "61a7e2b6742a43c73e8762cdfeaf6dfcf9abdd2cfa0b099a9854d69bc4cfee5c"

S = "${WORKDIR}/liberation-fonts-ttf-${PV}"

do_install:append () {
    install -d ${D}${datadir}/fonts/TTF/
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 LiberationSansNarrow*.ttf ${D}${datadir}/fonts/TTF/
    install -D -m 0644 ${UNPACKDIR}/30-0-liberation-sans-narrow.conf ${D}${sysconfdir}/conf.avail/30-${PN}-sans.conf
    install -D -m 0644 ${S}/License.txt ${D}${datadir}/licenses/${PN}/LICENSE
    # normal liberation fonts come from ttf-liberation recipe so delete here
    rm -rf ${D}${datadir}/fonts/truetype
}

PACKAGES = "${PN}"
FONT_PACKAGES = "${PN}"

FILES:${PN} = "${datadir}/fonts ${sysconfdir} ${datadir}/licenses"
