SUMMARY = "free PDF library"
HOMEPAGE = "http://libharu.org"
DESCRIPTION = "libHaru is a library for generating PDF files. \
               It is free, open source, written in ANSI C and cross platform. "

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=924546dab2bef90e370d7c0c090ddcf0"

DEPENDS += "libpng zlib"

SRC_URI = "git://github.com/libharu/libharu.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "3467749fd1c0ab6ca6ed424d053b1ea53c1bf67c"


inherit cmake
