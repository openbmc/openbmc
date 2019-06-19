SUMMARY = "Library for reading and editing the meta-data of popular audio formats"
SECTION = "libs/multimedia"
HOMEPAGE = "http://taglib.github.io/"
LICENSE = "LGPLv2.1 | MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.MPL;md5=bfe1f75d606912a4111c90743d6c7325 \
                    file://taglib/audioproperties.h;beginline=1;endline=24;md5=9df2c7399519b7310568a7c55042ecee"

DEPENDS = "zlib"

SRC_URI = "http://taglib.github.io/releases/${BP}.tar.gz \
           file://CVE-2017-12678.patch \
           file://CVE-2018-11439.patch \
          "

SRC_URI[md5sum] = "cee7be0ccfc892fa433d6c837df9522a"
SRC_URI[sha256sum] = "b6d1a5a610aae6ff39d93de5efd0fdc787aa9e9dc1e7026fa4c961b26563526b"

UPSTREAM_CHECK_URI = "http://github.com/taglib/taglib/releases/"

BINCONFIG = "${bindir}/taglib-config"

inherit cmake pkgconfig binconfig-disabled

PACKAGES =+ "${PN}-c"
FILES_${PN}-c = "${libdir}/libtag_c.so.*"

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON \
                 -DCMAKE_DISABLE_FIND_PACKAGE_Boost=TRUE \
                 -DHAVE_BOOST_BYTESWAP=FALSE \
                 -DCMAKE_CXX_STANDARD=11 \
                 -DCMAKE_CXX_STANDARD_REQUIRED=OFF \
                 -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"
CXXFLAGS += "-std=c++11"

do_configure_prepend () {
	rm -f ${S}/admin/ltmain.sh
	rm -f ${S}/admin/libtool.m4.in
}

# without -fPIC depending packages failed with many error like:
# | <...>/ld: error: <...>/usr/lib/libtag.a(modfilebase.cpp.o): requires unsupported dynamic reloc R_ARM_THM_MOVW_ABS_NC; recompile with -fPIC
CXXFLAGS += "-fPIC"
