SUMMARY = "Newlib is a C library intended for use on embedded systems"
HOMEPAGE = "https://sourceware.org/newlib/"
DESCRIPTION = "C library intended for use on embedded systems. It is a conglomeration of several library parts, all under free software licenses that make them easily usable on embedded products."
SECTION = "libs"

LICENSE = "GPL-2.0-only & LGPL-3.0-only & GPL-3.0-only & LGPL-2.0-only & BSD-2-Clause & BSD-3-Clause & TCL & Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = " \
		file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
		file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		file://COPYING.LIBGLOSS;md5=c0469b6ebb847a75781066be515f032d \
		file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
		file://COPYING.NEWLIB;md5=4f1a15846ffee91e352418563e1bce27 \
		file://newlib/libc/posix/COPYRIGHT;md5=103468ff1982be840fdf4ee9f8b51bbf \
		"

BASEVER = "4.4.0"
PV = "${BASEVER}+git"
SRC_URI = "git://sourceware.org/git/newlib-cygwin.git;protocol=https;branch=main"
SRCREV = "ad11e2587f83d61357a32c61c36d72ea4f39315e"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "virtual/cross-cc"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

## disable stdlib
TARGET_CC_ARCH:append = " -nostdlib"

# Both the C library and the application should share the same mcmodel.
# Use the medium-any code model for the RISC-V 64 bit implementation,
# since medlow can only access addresses below 0x80000000 and RAM
# starts at 0x80000000 on RISC-V 64
# Keep RISC-V 32 using -mcmodel=medlow (symbols lie between -2GB:2GB)
TARGET_CFLAGS:append:qemuriscv64 = " -mcmodel=medany"


EXTRA_OECONF = " \
                --build=${BUILD_SYS}  \
                --target=${TARGET_SYS} \
		--host=${HOST_SYS} \
                --prefix=${prefix}  \
                --exec-prefix=${exec_prefix} \
                --bindir=${bindir} \
                --libdir=${libdir} \
                --includedir=${includedir} \
		--enable-languages=c \
		--with-newlib \
		--with-gnu-as \
		--with-gnu-ld \
		--disable-multilib \
		--disable-newlib-supplied-syscalls \
		"

do_configure[cleandirs] = "${B}"

# We need a dummy limits.h to pass preprocessor checks
do_configure:append(){
    install -d ${STAGING_INCDIR}
    touch  ${STAGING_INCDIR}/limits.h
}

do_install() {
	oe_runmake install DESTDIR='${D}'
}

COMPATIBLE_HOST:libc-musl:class-target = "null"
COMPATIBLE_HOST:libc-glibc:class-target = "null"
