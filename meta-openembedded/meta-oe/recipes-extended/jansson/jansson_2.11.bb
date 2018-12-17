SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8b70213ec164c7bd876ec2120ba52f61"

SRC_URI = "http://www.digip.org/jansson/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "7af071db9970441e1eaaf25662310e33"
SRC_URI[sha256sum] = "6e85f42dabe49a7831dbdd6d30dca8a966956b51a9a50ed534b82afc3fa5b2f4"

inherit autotools pkgconfig

