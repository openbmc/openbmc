SUMMARY = "pimd is a lightweight stand-alone PIM-SM v2 multicast routing daemon."
HOMEPAGE = "http://troglobit.com/pimd.html"
SECTION = "net"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94f108f91fab720d62425770b70dd790"

SRC_URI = "ftp://troglobit.com/pimd/${BP}.tar.bz2"
SRC_URI[md5sum] = "a12448bc7c9bfcebf51a13ebf1ffa962"
SRC_URI[sha256sum] = "01016940543a0a6131d4e6c91b595d47e187012c59a298eec14c3dbc38564b3a"

EXTRA_OEMAKE = "-e MAKEFLAGS="
CFLAGS += "-I ${S}/include "

do_install() {
	unset datadir
	unset mandir
	oe_runmake 'DESTDIR=${D}' install
}

