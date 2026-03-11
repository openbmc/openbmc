SUMMARY = "free PDF library"
HOMEPAGE = "http://libharu.org"
DESCRIPTION = "libHaru is a library for generating PDF files. \
               It is free, open source, written in ANSI C and cross platform. "

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=924546dab2bef90e370d7c0c090ddcf0"

DEPENDS += "libpng zlib"

SRC_URI = "git://github.com/libharu/libharu.git;branch=master;protocol=https"
SRCREV = "8fe5a738541a04642885fb7a75b2b5b9c5b416fa"


inherit cmake
