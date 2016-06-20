SUMMARY = "Library for reading and editing the meta-data of popular audio formats"
SECTION = "libs/multimedia"
HOMEPAGE = "http://taglib.github.io/"
LICENSE = "LGPLv2.1 | MPL-1"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.MPL;md5=bfe1f75d606912a4111c90743d6c7325 \
                    file://taglib/audioproperties.h;beginline=1;endline=24;md5=9df2c7399519b7310568a7c55042ecee"

DEPENDS = "zlib"

SRC_URI = "http://taglib.github.io/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "0d35df96822bbd564c5504cb3c2e4d86"
SRC_URI[sha256sum] = "72d371cd1419a87ae200447a53bff2be219283071e80fd12337928cc967dc71a"

UPSTREAM_CHECK_URI = "http://github.com/taglib/taglib/releases/"

BINCONFIG = "${bindir}/taglib-config"

inherit cmake pkgconfig binconfig-disabled

PACKAGES =+ "${PN}-c"
FILES_${PN}-c = "${libdir}/libtag_c.so.*"

EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib', True).replace('lib', '')}"

do_configure_prepend () {
	rm -f ${S}/admin/ltmain.sh
	rm -f ${S}/admin/libtool.m4.in
	# Don't have a floating dependeny on boost
	sed -i -e "s/atomic.hpp/atomic-not-exist.hpp/" ${S}/ConfigureChecks.cmake ${S}/taglib/toolkit/trefcounter.cpp
}

