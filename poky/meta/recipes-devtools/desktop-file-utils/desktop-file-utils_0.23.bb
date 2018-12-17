SECTION = "console/utils"
SUMMARY = "Command line utilities for working with *.desktop files"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/desktop-file-utils"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/validator.c;beginline=4;endline=27;md5=281e1114ee6c486a1a0a4295986b9416"

SRC_URI = "http://freedesktop.org/software/${BPN}/releases/${BP}.tar.xz"
SRC_URI[md5sum] = "599133d51cc9a41bfec186414906b6f1"
SRC_URI[sha256sum] = "6c094031bdec46c9f621708f919084e1cb5294e2c5b1e4c883b3e70cb8903385"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

EXTRA_OECONF += "ac_cv_prog_EMACS=no"

BBCLASSEXTEND = "native nativesdk"
