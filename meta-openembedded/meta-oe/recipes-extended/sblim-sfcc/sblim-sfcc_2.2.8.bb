SUMMARY = "Small Footprint CIM Client Library"
DESCRIPTION = "Small Footprint CIM Client Library Runtime Libraries"
HOMEPAGE = "http://www.sblim.org"

SRC_URI = "http://netcologne.dl.sourceforge.net/project/sblim/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://0001-cimxml-Include-sys-select.h-for-fd_set.patch \
           file://0001-Fix-implicit-function-declarations.patch \
           "

SRC_URI[md5sum] = "0bac0dec19f17ec065b6c332a56d7bae"
SRC_URI[sha256sum] = "1b8f187583bc6c6b0a63aae0165ca37892a2a3bd4bb0682cd76b56268b42c3d6"

LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f300afd598546add034364cd0a533261"

DEPENDS = "curl"

inherit autotools
