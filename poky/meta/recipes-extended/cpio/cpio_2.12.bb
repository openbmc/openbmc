require cpio_v2.inc

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = "${GNU_MIRROR}/cpio/cpio-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0001-Fix-CVE-2015-1197.patch \
           "

SRC_URI[md5sum] = "fc207561a86b63862eea4b8300313e86"
SRC_URI[sha256sum] = "08a35e92deb3c85d269a0059a27d4140a9667a6369459299d08c17f713a92e73"
