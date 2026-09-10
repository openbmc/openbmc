SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
HOMEPAGE = "http://www.nasm.us/"
DESCRIPTION = "The Netwide Assembler (NASM) is an assembler and disassembler for the Intel x86 architecture."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6178dc4f5355e40552448080e67a214b"

DEPENDS = "zlib"

SRC_URI = "http://www.nasm.us/pub/nasm/releasebuilds/${PV}/nasm-${PV}.tar.bz2 \
           file://0001-stdlib-Add-strlcat.patch \
           file://0002-Add-debug-prefix-map-option.patch \
           "

SRC_URI[sha256sum] = "ce7ed93281615379e4a9d4e76503c64a79c1d5ca696dbe148f16cf0b93e239af"

EXTRA_AUTORECONF:append = " -I autoconf/m4"

inherit autotools-brokensep

BBCLASSEXTEND = "native"

CVE_PRODUCT = "netwide_assembler"
