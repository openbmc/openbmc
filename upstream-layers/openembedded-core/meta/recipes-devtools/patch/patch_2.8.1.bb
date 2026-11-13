require patch.inc
LICENSE = "GPL-3.0-only"

CVE_PRODUCT = "gnu:patch"

SRC_URI[sha256sum] = "3240c7959c5347c8ed347e3c4201c0337bf4240d5cf09d68889e58eeaba30e17"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'xattr', d)}"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"

PROVIDES:append:class-native = " patch-replacement-native"

BBCLASSEXTEND = "native nativesdk"
