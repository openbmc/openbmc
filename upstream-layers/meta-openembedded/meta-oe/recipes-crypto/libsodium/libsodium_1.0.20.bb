SUMMARY = "The Sodium crypto library"
HOMEPAGE = "http://libsodium.org/"
BUGTRACKER = "https://github.com/jedisct1/libsodium/issues"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c59be7bb29f8e431b5f2d690b6734185"

SRC_URI = "https://download.libsodium.org/libsodium/releases/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "ebb65ef6ca439333c2bb41a0c1990587288da07f6c7fd07cb3a18cc18d30ce19"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
