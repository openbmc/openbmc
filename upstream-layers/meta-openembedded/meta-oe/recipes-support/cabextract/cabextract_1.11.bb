SUMMARY = "Software for extracting Microsoft cabinet files"
DESCRIPTION = "tool for extracting Microsoft cabinet files"
HOMEPAGE = "http://www.cabextract.org.uk/"
SECTION = "console/utils"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://src/cabextract.c;beginline=4;endline=11;md5=b0a10c6d3843f262114e7ecf91fc7e78"

SRC_URI = "\
    https://www.cabextract.org.uk/cabextract-${PV}.tar.gz \
    file://fix-fnmatch.patch \
"

SRC_URI[sha256sum] = "b5546db1155e4c718ff3d4b278573604f30dd64c3c5bfd4657cd089b823a3ac6"

DEPENDS = "libmspack"

EXTRA_OECONF = "--with-external-libmspack"

inherit autotools pkgconfig

BBCLASSEXTEND += "native nativesdk"
