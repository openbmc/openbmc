SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b70213ec164c7bd876ec2120ba52f61"

SRC_URI = "http://www.digip.org/jansson/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "84abaefee9502b2f2ff394d758f160c7"
SRC_URI[sha256sum] = "0ad0d074ca049a36637e7abef755d40849ad73e926b93914ce294927b97bd2a5"

inherit autotools pkgconfig

