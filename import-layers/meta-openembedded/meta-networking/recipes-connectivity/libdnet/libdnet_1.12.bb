SUMMARY = "dumb networking library"
HOMEPAGE = "http://code.google.com/p/libdnet/"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0036c1b155f4e999f3e0a373490b5db9"

SRC_URI = "http://libdnet.googlecode.com/files/libdnet-${PV}.tgz"

SRC_URI[md5sum] = "9253ef6de1b5e28e9c9a62b882e44cc9"
SRC_URI[sha256sum] = "83b33039787cf99990e977cef7f18a5d5e7aaffc4505548a83d31bd3515eb026"

inherit autotools

acpaths = "-I ./config/"

