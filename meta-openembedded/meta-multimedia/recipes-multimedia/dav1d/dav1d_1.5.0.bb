SUMMARY = "dav1d AV1 decoder"
DESCRIPTION = "Targeted to be small, portable and fast."
HOMEPAGE = "https://code.videolan.org/videolan/dav1d"
SECTION = "multimedia"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://COPYING;md5=c8055cfe7548dfdaa3a6dc45d8793669"

SRC_URI = "git://code.videolan.org/videolan/dav1d.git;protocol=https;nobranch=1"
SRCREV = "32cf02af50f32af108a3b281c452788dccdac648"

S = "${WORKDIR}/git"

DEPENDS:append:x86 = " nasm-native"
DEPENDS:append:x86-64 = " nasm-native"

inherit meson pkgconfig

# RVV assembler routines are not yet available for RISCV32
EXTRA_OEMESON:append:riscv32 = " -Denable_asm=false"
