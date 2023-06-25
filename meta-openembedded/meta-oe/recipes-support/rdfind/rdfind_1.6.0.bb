SUMMARY = "Rdfind is a program that finds duplicate files"
HOMEPAGE = "https://rdfind.pauldreik.se/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa22e16ebbe6638b2bd253338fbded9f"

DEPENDS = "nettle autoconf-archive"

SRC_URI = "https://rdfind.pauldreik.se/${BP}.tar.gz"
SRC_URI[sha256sum] = "7a406e8ef1886a5869655604618dd98f672f12c6a6be4926d053be65070f3279"

inherit autotools

BBCLASSEXTEND = "native"
