SUMMARY = "utilities to create, check, label and dump exFAT filesystem"
DESCRIPTION = "Utilities to manage extended file allocation table filesystem. \
This package provides tools to create, check and label the filesystem. It \
contains \
 - dumpexfat to dump properties of the filesystem \
 - exfatfsck / fsck.exfat to report errors found on a exFAT filesystem \
 - exfatlabel to label a exFAT filesystem \
 - mkexfatfs / mkfs.exfat to create a exFAT filesystem. \
"
HOMEPAGE = "http://code.google.com/p/exfat/"
SECTION = "universe/otherosfs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
SRC_URI = "${DEBIAN_MIRROR}/main/e/exfat-utils/exfat-utils_${PV}.orig.tar.gz \
"
DEPENDS = "virtual/libc"

inherit pkgconfig autotools

SRC_URI[md5sum] = "f4e564450aa8159e26dde2869563d242"
SRC_URI[sha256sum] = "80d3b3f21242d60d36a38a4ddb05cb7cc3a7d4eef5793e8314814937b938fcea"
