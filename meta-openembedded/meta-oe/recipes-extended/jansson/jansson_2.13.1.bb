SUMMARY = "Jansson is a C library for encoding, decoding and manipulating JSON data"
HOMEPAGE = "http://www.digip.org/jansson/"
BUGTRACKER = "https://github.com/akheron/jansson/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=afd92c4cfc08f4896003251b878cc0bf"

SRC_URI = "http://www.digip.org/jansson/releases/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "570af45b8203e95876d71fecd56cee20"
SRC_URI[sha256sum] = "f4f377da17b10201a60c1108613e78ee15df6b12016b116b6de42209f47a474f"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
