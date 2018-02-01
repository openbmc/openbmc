SUMMARY = "A coroutine-based Python networking library"
DESCRIPTION = "gevent is a coroutine-based Python networking library that uses greenlet to provide \
a high-level synchronous API on top of the libevent event loop."
HOMEPAGE = "http://www.gevent.org"
LICENSE = "MIT & Python-2.0 & BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2dbb33d00e1fd31c7041460a81ac0bd2 \
                    file://NOTICE;md5=5966cd2c6582656d28ab3c33da3860f8 \
                    file://deps/libev/LICENSE;md5=d6ad416afd040c90698edcdf1cbee347"
DEPENDS += "python-greenlet libevent"
RDEPENDS_${PN} += "python-greenlet python-mime python-pprint python-re"

SRC_URI_append = " \
    file://libev-conf.patch;patch=1;pnum=1 \
"

SRC_URI[md5sum] = "6700a2433c8e0635425e6798760efc81"
SRC_URI[sha256sum] = "3de300d0e32c31311e426e4d5d73b36777ed99c2bac3f8fbad939eeb2c29fa7c"

# The python-gevent has no autoreconf ability
# and the logic for detecting a cross compile is flawed
# so always force a cross compile
do_configure_append() {
	sed -i -e 's/^cross_compiling=no/cross_compiling=yes/' ${S}/deps/libev/configure
	sed -i -e 's/^cross_compiling=no/cross_compiling=yes/' ${S}/deps/c-ares/configure
}

inherit pypi setuptools

