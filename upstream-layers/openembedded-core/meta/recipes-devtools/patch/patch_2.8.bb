require patch.inc
LICENSE = "GPL-3.0-only"

SRC_URI[sha256sum] = "308a4983ff324521b9b21310bfc2398ca861798f02307c79eb99bb0e0d2bf980"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'xattr', d)}"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"

PROVIDES:append:class-native = " patch-replacement-native"

BBCLASSEXTEND = "native nativesdk"
