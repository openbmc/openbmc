SECTION = "console/utils"
SUMMARY = "Command line utilities for working with *.desktop files"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/desktop-file-utils"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0-native"

SRC_URI = "http://freedesktop.org/software/desktop-file-utils/releases/desktop-file-utils-${PV}.tar.xz"

SRC_URI[md5sum] = "599133d51cc9a41bfec186414906b6f1"
SRC_URI[sha256sum] = "6c094031bdec46c9f621708f919084e1cb5294e2c5b1e4c883b3e70cb8903385"

inherit autotools native

S = "${WORKDIR}/desktop-file-utils-${PV}"

EXTRA_OECONF += "ac_cv_prog_EMACS=no"
