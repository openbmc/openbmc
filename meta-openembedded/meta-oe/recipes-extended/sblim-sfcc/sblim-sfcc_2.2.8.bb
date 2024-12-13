SUMMARY = "Small Footprint CIM Client Library"
DESCRIPTION = "Small Footprint CIM Client Library Runtime Libraries"
HOMEPAGE = "http://www.sblim.org"

SRC_URI = "http://netcologne.dl.sourceforge.net/project/sblim/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://0001-cimxml-Include-sys-select.h-for-fd_set.patch \
           file://0001-Fix-implicit-function-declarations.patch \
           file://0001-Fix-incompatible-pointer-type-error-with-gcc-option.patch \
           "

SRC_URI[md5sum] = "0bac0dec19f17ec065b6c332a56d7bae"
SRC_URI[sha256sum] = "1b8f187583bc6c6b0a63aae0165ca37892a2a3bd4bb0682cd76b56268b42c3d6"

UPSTREAM_CHECK_URI="https://sourceforge.net/projects/sblim/files/sblim-sfcc/"

LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f300afd598546add034364cd0a533261"

DEPENDS = "curl"

inherit autotools

# http://errors.yoctoproject.org/Errors/Details/766897/
# sblim-sfcc-2.2.8/TEST/v2test_ec.c:86:31: error: passing argument 1 of 'showClass' from incompatible pointer type [-Wincompatible-pointer-types]
# sblim-sfcc-2.2.8/TEST/v2test_ein.c:96:36: error: passing argument 1 of 'showObjectPath' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
