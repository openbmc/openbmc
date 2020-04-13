SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 \
           file://CVE-2018-19755.patch \
           file://CVE-2019-14248.patch \
           file://0001-stdlib-Add-strlcat.patch \
           file://0002-Add-debug-prefix-map-option.patch \
           "

SRC_URI[md5sum] = "3f489aa48ad2aa1f967dc5e293bbd06f"
SRC_URI[sha256sum] = "34fd26c70a277a9fdd54cb5ecf389badedaf48047b269d1008fbc819b24e80bc"

# brokensep since this uses autoconf but not automake
inherit autotools-brokensep

EXTRA_AUTORECONF += "--exclude=aclocal"

BBCLASSEXTEND = "native"

DEPENDS = "groff-native"

CVE_PRODUCT = "netwide_assembler"
