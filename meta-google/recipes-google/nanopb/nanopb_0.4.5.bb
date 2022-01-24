SUMMARY = "Nanopb library"
DESCRIPTION = "Nanopb - Protocol Buffers for Embedded Systems"
HOMEPAGE = "https://github.com/nanopb/nanopb"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

inherit cmake python3native

SRC_URI = "git://github.com/nanopb/nanopb;branch=master;protocol=https"
SRCREV = "f7e4140a27d9e63517b5d596bc117bd6d5248888"
S = "${WORKDIR}/git"

DEPENDS = "protobuf-native python3-protobuf"

RDEPENDS:${PN}-generator += "python3 python3-protobuf"

PACKAGES:prepend = "${PN}-generator ${PN}-runtime "

FILES:${PN}-generator = "${libdir}/python* ${bindir}"

FILES:${PN}-runtime = "${libdir}/*.so.*"

BBCLASSEXTEND = "native"
