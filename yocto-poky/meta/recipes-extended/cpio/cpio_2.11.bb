include cpio_v2.inc

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

PR = "r5"

SRC_URI += "file://remove-gets.patch \
        file://fix-memory-overrun.patch \
        file://cpio-CVE-2015-1197.patch \
        file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
        "

SRC_URI[md5sum] = "1112bb6c45863468b5496ba128792f6c"
SRC_URI[sha256sum] = "601b1d774cd6e4cd39416203c91ec59dbd65dd27d79d75e1a9b89497ea643978"
