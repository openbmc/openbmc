SUMMARY = "Rdfind is a program that finds duplicate files"
HOMEPAGE = "https://rdfind.pauldreik.se/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fa22e16ebbe6638b2bd253338fbded9f"

DEPENDS = "nettle autoconf-archive"

SRC_URI = "https://rdfind.pauldreik.se/${BP}.tar.gz \
           file://0001-configure-Fix-check-for-AC_CHECK_LIB.patch \
           file://0001-include-standard-headers-limits-and-cstdint.patch \
"

SRC_URI[sha256sum] = "4150ed1256f7b12b928c65113c485761552b9496c433778aac3f9afc3e767080"

inherit autotools

BBCLASSEXTEND = "native"
