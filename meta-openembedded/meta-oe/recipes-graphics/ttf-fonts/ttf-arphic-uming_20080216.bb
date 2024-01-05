SUMMARY = "Unicode Mingti (printed) TrueType Font"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/CJKUnifonts"
LICENSE = "Arphic-Public-License"
LIC_FILES_CHKSUM = "file://license/english/ARPHICPL.TXT;md5=4555ed88e9a72fc9562af379d07c3350"

RPROVIDES:${PN} = "virtual-chinese-font"
PR = "r6"

FONT_PACKAGES = "${PN}"

SRC_URI = "https://deb.debian.org/debian/pool/main/f/fonts-arphic-uming/fonts-arphic-uming_0.2.${PV}.2.orig.tar.bz2"
S = "${WORKDIR}/ttf-arphic-uming-0.2.20080216.2"

require ttf.inc

FILES:${PN} = "${datadir}"

SRC_URI[sha256sum] = "e3c19e04ea7a565b4acff6f1e4248084d2e10752e305bf7dd6c76e80860dc1db"
