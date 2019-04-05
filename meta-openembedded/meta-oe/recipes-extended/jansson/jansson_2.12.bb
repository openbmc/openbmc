SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fc2548c0eb83800f29330040e18b5a05"

SRC_URI = "http://www.digip.org/jansson/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "0ed1f3a924604aae68067c214b0010ef"
SRC_URI[sha256sum] = "5f8dec765048efac5d919aded51b26a32a05397ea207aa769ff6b53c7027d2c9"

inherit autotools pkgconfig

