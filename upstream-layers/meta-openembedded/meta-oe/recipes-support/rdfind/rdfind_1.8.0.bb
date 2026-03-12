SUMMARY = "Rdfind is a program that finds duplicate files"
HOMEPAGE = "https://rdfind.pauldreik.se/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa22e16ebbe6638b2bd253338fbded9f"

DEPENDS = "nettle autoconf-archive"

SRC_URI = "https://rdfind.pauldreik.se/${BP}.tar.gz"
SRC_URI[sha256sum] = "0a2d0d32002cc2dc0134ee7b649bcc811ecfb2f8d9f672aa476a851152e7af35"

inherit autotools

BBCLASSEXTEND = "native"
