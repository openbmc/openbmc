SUMMARY = "Library for reading and editing the meta-data of popular audio formats"
DESCRIPTION = "Platform-independent library (tested on Windows/Linux) for reading and writing metadata in media files, including video, audio, and photo formats. This is a convenient one-stop-shop to present or tag all your media collection, regardless of which format/container these might use. You can read/write the standard or more common tags/properties of a media, or you can also create and retrieve your own custom tags."
SECTION = "libs/multimedia"
HOMEPAGE = "http://taglib.github.io/"
LICENSE = "LGPL-2.1-only | MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.MPL;md5=bfe1f75d606912a4111c90743d6c7325 \
                    file://taglib/audioproperties.h;beginline=1;endline=24;md5=9df2c7399519b7310568a7c55042ecee"

DEPENDS = "zlib utfcpp"

SRC_URI = "http://taglib.github.io/releases/${BP}.tar.gz"

SRC_URI[sha256sum] = "e36ea877a6370810b97d84cf8f72b1e4ed205149ab3ac8232d44c850f38a2859"

UPSTREAM_CHECK_URI = "https://taglib.org/"
UPSTREAM_CHECK_REGEX = "taglib-(?P<pver>\d+(\.\d+)+)\.tar"

BINCONFIG = "${bindir}/taglib-config"

inherit cmake pkgconfig binconfig-disabled

PACKAGES =+ "${PN}-c"
FILES:${PN}-c = "${libdir}/libtag_c.so.*"

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON \
                 -DCMAKE_DISABLE_FIND_PACKAGE_Boost=TRUE \
                 -DHAVE_BOOST_BYTESWAP=FALSE \
                 -DCMAKE_CXX_STANDARD=11 \
                 -DCMAKE_CXX_STANDARD_REQUIRED=OFF \
                 -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
"
CXXFLAGS += "-std=c++11"

do_configure:prepend () {
	rm -f ${S}/admin/ltmain.sh
	rm -f ${S}/admin/libtool.m4.in
}

# without -fPIC depending packages failed with many error like:
# | <...>/ld: error: <...>/usr/lib/libtag.a(modfilebase.cpp.o): requires unsupported dynamic reloc R_ARM_THM_MOVW_ABS_NC; recompile with -fPIC
CXXFLAGS += "-fPIC"
