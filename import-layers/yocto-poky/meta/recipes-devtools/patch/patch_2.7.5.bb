require patch.inc
LICENSE = "GPLv3"

SRC_URI += "file://0001-Unset-need_charset_alias-when-building-for-musl.patch"

SRC_URI[md5sum] = "ed4d5674ef4543b4eb463db168886dc7"
SRC_URI[sha256sum] = "7436f5a19f93c3ca83153ce9c5cbe4847e97c5d956e57a220121e741f6e7968f"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

acpaths = "-I ${S}/m4 "

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'xattr', '', d)}"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"

