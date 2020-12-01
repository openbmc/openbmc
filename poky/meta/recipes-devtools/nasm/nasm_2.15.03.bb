SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 \
           file://0001-stdlib-Add-strlcat.patch \
           file://0002-Add-debug-prefix-map-option.patch \
           "

SRC_URI[sha256sum] = "04e7343d9bf112bffa9fda86f6c7c8b120c2ccd700b882e2db9f57484b1bd778"

EXTRA_AUTORECONF_append = " -I autoconf/m4"

inherit autotools

BBCLASSEXTEND = "native"

DEPENDS = "groff-native"

CVE_PRODUCT = "netwide_assembler"
