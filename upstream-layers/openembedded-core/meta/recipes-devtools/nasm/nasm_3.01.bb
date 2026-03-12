SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
HOMEPAGE = "http://www.nasm.us/"
DESCRIPTION = "The Netwide Assembler (NASM) is an assembler and disassembler for the Intel x86 architecture."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6178dc4f5355e40552448080e67a214b"

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 \
           file://0001-stdlib-Add-strlcat.patch \
           file://0002-Add-debug-prefix-map-option.patch \
           file://0001-compiler.h-Backport-C23-support.patch \
           "

SRC_URI[sha256sum] = "7a7b1ff3b0eef3247862f2fbe4ca605ccef770545d7af7979eba84a9d045c0b1"

EXTRA_AUTORECONF:append = " -I autoconf/m4"

inherit autotools-brokensep

BBCLASSEXTEND = "native"

DEPENDS = "groff-native"

CVE_PRODUCT = "netwide_assembler"
