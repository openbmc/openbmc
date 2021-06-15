SUMMARY = "Rdfind is a program that finds duplicate files"
HOMEPAGE = "https://rdfind.pauldreik.se/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa22e16ebbe6638b2bd253338fbded9f"

DEPENDS = "nettle autoconf-archive"

SRC_URI = "https://rdfind.pauldreik.se/${BP}.tar.gz \
"

SRC_URI[md5sum] = "180418c863b861d1df221bc486a07ce7"
SRC_URI[sha256sum] = "30c613ec26eba48b188d2520cfbe64244f3b1a541e60909ce9ed2efb381f5e8c"

inherit autotools

BBCLASSEXTEND = "native"
