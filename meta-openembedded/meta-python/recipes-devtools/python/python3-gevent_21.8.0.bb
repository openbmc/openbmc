SUMMARY = "A coroutine-based Python networking library"
DESCRIPTION = "gevent is a coroutine-based Python networking library that uses greenlet to provide \
a high-level synchronous API on top of the libevent event loop."
HOMEPAGE = "http://www.gevent.org"
LICENSE = "MIT & Python-2.0 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4de99aac27b470c29c6c309e0c279b65 \
                    file://NOTICE;md5=18108df3583462cafd457f024b9b09b5 \
                    file://deps/libev/LICENSE;md5=d6ad416afd040c90698edcdf1cbee347 \
                    "
DEPENDS += "libevent"
DEPENDS += "${PYTHON_PN}-greenlet"
RDEPENDS:${PN} = "${PYTHON_PN}-greenlet \
		  ${PYTHON_PN}-mime \
		  ${PYTHON_PN}-pprint \
		 "

FILESEXTRAPATHS:prepend := "${THISDIR}/python-gevent:"

SRC_URI:append = " \
    file://libev-conf.patch;patch=1;pnum=1 \
"

SRC_URI[sha256sum] = "43e93e1a4738c922a2416baf33f0afb0a20b22d3dba886720bc037cd02a98575"

# The python-gevent has no autoreconf ability
# and the logic for detecting a cross compile is flawed
# so always force a cross compile
do_configure:append() {
	sed -i -e 's/^cross_compiling=no/cross_compiling=yes/' ${S}/deps/libev/configure
	sed -i -e 's/^cross_compiling=no/cross_compiling=yes/' ${S}/deps/c-ares/configure
}

inherit pypi setuptools3
