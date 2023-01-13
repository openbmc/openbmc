SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
HOMEPAGE = "http://www.nasm.us/"
DESCRIPTION = "The Netwide Assembler (NASM) is an assembler and disassembler for the Intel x86 architecture."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 \
           file://0001-stdlib-Add-strlcat.patch \
           file://0002-Add-debug-prefix-map-option.patch \
           "

SRC_URI[sha256sum] = "35b6ad2ee048d41c4779f073f3efca7762a822b7d2d4ef4e8df24cf65747bb2e"

EXTRA_AUTORECONF:append = " -I autoconf/m4"

inherit autotools-brokensep

BBCLASSEXTEND = "native"

DEPENDS = "groff-native"

CVE_PRODUCT = "netwide_assembler"
