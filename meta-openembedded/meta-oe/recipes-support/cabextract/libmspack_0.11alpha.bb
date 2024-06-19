SUMMARY = "A library for Microsoft compression formats"
DESCRIPTION = "The library provides compressors and decompressors,\
archivers and dearchivers for Microsoft compression formats: CAB, CHM, WIM,\
LIT, HLP, KWAJ and SZDD."
HOMEPAGE = "http://www.cabextract.org.uk/libmspack/"
SECTION = "lib"
LICENSE = "LGPL-2.1-only"

LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "https://www.cabextract.org.uk/libmspack/libmspack-${PV}.tar.gz"
SRC_URI[sha256sum] = "70dd1fb2f0aecc36791b71a1e1840e62173079eadaa081192d1c323a0eeea21b"

inherit autotools

BBCLASSEXTEND += "native nativesdk"
