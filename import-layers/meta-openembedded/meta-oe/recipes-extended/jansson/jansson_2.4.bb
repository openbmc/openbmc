SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6946b728e700de875e60ebb453cc3a20"

SRC_URI = "http://www.digip.org/jansson/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "c4629b89bf0432f3158c461e88fe0113"
SRC_URI[sha256sum] = "1fcbd1ac3d8b610644acf86a5731d760bb228c9acbace20a2ad0f23baec79b41"

inherit autotools pkgconfig

