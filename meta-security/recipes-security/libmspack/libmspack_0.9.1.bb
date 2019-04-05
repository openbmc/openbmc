SUMMARY = "A library for Microsoft compression formats"
HOMEPAGE = "http://www.cabextract.org.uk/libmspack/"
SECTION = "lib"
LICENSE = "LGPL-2.1"
DEPENDS = ""

LIC_FILES_CHKSUM = "file://COPYING.LIB;beginline=1;endline=2;md5=5b1fd1f66ef926b3c8a5bb00a72a28dd"

SRC_URI = "${DEBIAN_MIRROR}/main/libm/${BPN}/${BPN}_${PV}.orig.tar.gz"

SRC_URI[md5sum] = "9602ae4a6b0468d9aaef6359c1e90657"
SRC_URI[sha256sum] = "62a336d9c798638aaf3dceb43843320061544bbf35547c316b075b99112f2e40"

inherit autotools

S = "${WORKDIR}/${BP}alpha"
