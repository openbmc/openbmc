SUMMARY = "A library for Microsoft compression formats"
HOMEPAGE = "http://www.cabextract.org.uk/libmspack/"
SECTION = "lib"
LICENSE = "LGPL-2.1"
DEPENDS = ""

LIC_FILES_CHKSUM = "file://COPYING.LIB;beginline=1;endline=2;md5=5b1fd1f66ef926b3c8a5bb00a72a28dd"

SRCREV = "63d3faf90423a4a6c174539a7d32111a840adadc"
SRC_URI = "git://github.com/kyz/libmspack.git"

inherit autotools

S = "${WORKDIR}/git/${BPN}"

inherit autotools
