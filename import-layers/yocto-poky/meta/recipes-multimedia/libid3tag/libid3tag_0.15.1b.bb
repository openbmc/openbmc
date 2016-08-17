SUMMARY = "Library for interacting with ID3 tags in MP3 files"
HOMEPAGE = "http://sourceforge.net/projects/mad/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=12349&atid=112349"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
			file://COPYRIGHT;md5=5e6279efb87c26c6e5e7a68317a6a87a \
			file://version.h;beginline=1;endline=8;md5=86ac68b67f054b7afde9e149bbc3fe63"
SECTION = "libs"
DEPENDS = "zlib gperf-native"
PR = "r7"

SRC_URI = "ftp://ftp.mars.org/pub/mpeg/libid3tag-${PV}.tar.gz \
           file://addpkgconfig.patch \
           file://obsolete_automake_macros.patch \
"

SRC_URI[md5sum] = "e5808ad997ba32c498803822078748c3"
SRC_URI[sha256sum] = "63da4f6e7997278f8a3fef4c6a372d342f705051d1eeb6a46a86b03610e26151"

S = "${WORKDIR}/libid3tag-${PV}"

inherit autotools pkgconfig
