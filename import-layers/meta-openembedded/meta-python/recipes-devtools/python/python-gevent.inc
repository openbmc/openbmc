SUMMARY = "A coroutine-based Python networking library"
DESCRIPTION = "gevent is a coroutine-based Python networking library that uses greenlet to provide \
a high-level synchronous API on top of the libevent event loop."
HOMEPAGE = "http://www.gevent.org"
LICENSE = "MIT & Python-2.0 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4de99aac27b470c29c6c309e0c279b65 \
                    file://NOTICE;md5=5966cd2c6582656d28ab3c33da3860f8 \
                    file://deps/libev/LICENSE;md5=d6ad416afd040c90698edcdf1cbee347"
DEPENDS += "libevent"
DEPENDS += "${PYTHON_PN}-greenlet"
RDEPENDS_${PN} = "${PYTHON_PN}-greenlet \
		  ${PYTHON_PN}-mime \
		  ${PYTHON_PN}-pprint \
		  ${PYTHON_PN}-re \
		 "

FILESEXTRAPATHS_prepend := "${THISDIR}/python-gevent:"

SRC_URI_append = " \
    file://libev-conf.patch;patch=1;pnum=1 \
"

SRC_URI[md5sum] = "7f0baf355384fe5ff2ecf66853422554"
SRC_URI[sha256sum] = "4791c8ae9c57d6f153354736e1ccab1e2baf6c8d9ae5a77a9ac90f41e2966b2d"

# The python-gevent has no autoreconf ability
# and the logic for detecting a cross compile is flawed
# so always force a cross compile
do_configure_append() {
	sed -i -e 's/^cross_compiling=no/cross_compiling=yes/' ${S}/deps/libev/configure
	sed -i -e 's/^cross_compiling=no/cross_compiling=yes/' ${S}/deps/c-ares/configure
}

inherit pypi
