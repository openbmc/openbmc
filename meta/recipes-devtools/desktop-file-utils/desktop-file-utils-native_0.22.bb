SECTION = "console/utils"
SUMMARY = "Command line utilities for working with *.desktop files"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/desktop-file-utils"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0-native"

SRC_URI = "http://freedesktop.org/software/desktop-file-utils/releases/desktop-file-utils-${PV}.tar.xz"

SRC_URI[md5sum] = "c6b9f9aac1ea143091178c23437e6cd0"
SRC_URI[sha256sum] = "843532672692f98e9b2d6ae6cc8658da562dfde1606c7f33d9d227a344de56c5"

inherit autotools native

S = "${WORKDIR}/desktop-file-utils-${PV}"
