SUMMARY = "dav1d AV1 decoder"
DESCRIPTION = "Targeted to be small, portable and fast."
HOMEPAGE = "https://code.videolan.org/videolan/dav1d"
SECTION = "multimedia"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://COPYING;md5=48d9210a87a24f62609b5a4f777d8dfe"

SRC_URI = "git://code.videolan.org/videolan/dav1d.git;protocol=https;nobranch=1"
SRCREV = "b546257f770768b2c88258c533da38b91a06f737"

DEPENDS:append:x86 = " nasm-native"
DEPENDS:append:x86-64 = " nasm-native"

inherit meson pkgconfig

# RVV assembler routines are not yet available for RISCV32
EXTRA_OEMESON:append:riscv32 = " -Denable_asm=false"
