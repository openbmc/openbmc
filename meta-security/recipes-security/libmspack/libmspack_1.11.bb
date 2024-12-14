SUMMARY = "A library for Microsoft compression formats"
HOMEPAGE = "http://www.cabextract.org.uk/libmspack/"
SECTION = "lib"
LICENSE = "LGPL-2.1-only"
DEPENDS = ""

LIC_FILES_CHKSUM = "file://COPYING.LIB;beginline=1;endline=2;md5=5b1fd1f66ef926b3c8a5bb00a72a28dd"

SRCREV = "305907723a4e7ab2018e58040059ffb5e77db837"
SRC_URI = "git://github.com/kyz/libmspack.git;branch=master;protocol=https"

inherit autotools

S = "${UNPACKDIR}/git/${BPN}"

inherit autotools
