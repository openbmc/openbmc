SUMMARY = "Library handling the communication with Apple's Tatsu Signing Server (TSS)"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ab17b41640564434dda85c06b7124f7"

SRC_URI = "https://github.com/libimobiledevice/libtatsu/releases/download/${PV}/libtatsu-${PV}.tar.bz2"
SRC_URI[sha256sum] = "536fa228b14f156258e801a7f4d25a3a9dd91bb936bf6344e23171403c57e440"

DEPENDS = "curl libplist"

inherit pkgconfig autotools
