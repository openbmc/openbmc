SUMMARY = "Command line utilities for working with *.desktop files"
DESCRIPTION = "desktop-file-utils contains a few command line utilities for working with desktop entries"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/desktop-file-utils"
SECTION = "console/utils"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/validator.c;beginline=4;endline=27;md5=281e1114ee6c486a1a0a4295986b9416"

SRC_URI = "http://freedesktop.org/software/${BPN}/releases/${BP}.tar.xz"
SRC_URI[md5sum] = "29739e005f5887cf41639b8450f3c23f"
SRC_URI[sha256sum] = "b26dbde79ea72c8c84fb7f9d870ffd857381d049a86d25e0038c4cef4c747309"

DEPENDS = "glib-2.0"

inherit autotools pkgconfig

EXTRA_OECONF += "ac_cv_prog_EMACS=no"

BBCLASSEXTEND = "native nativesdk"

do_install_append() {
        rm -rf ${D}${datadir}/emacs
}

