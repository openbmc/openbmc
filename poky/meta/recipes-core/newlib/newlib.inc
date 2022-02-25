SUMMARY = "Newlib is a C library intended for use on embedded systems"
HOMEPAGE = "https://sourceware.org/newlib/"
DESCRIPTION = "C library intended for use on embedded systems. It is a conglomeration of several library parts, all under free software licenses that make them easily usable on embedded products."
SECTION = "libs"

LICENSE = "GPL-2.0-only & LGPL-3.0-only & GPL-3.0-only & LGPL-2.0-only & BSD-2-Clause & BSD-3-Clause & TCL"
LIC_FILES_CHKSUM = " \
		file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
		file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
		file://COPYING.LIBGLOSS;md5=c0469b6ebb847a75781066be515f032d \
		file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
		file://COPYING.NEWLIB;md5=8bb75595dbcc7d45e5c0d116d7bdf6ce \
		file://newlib/libc/posix/COPYRIGHT;md5=103468ff1982be840fdf4ee9f8b51bbf \
		file://newlib/libc/sys/linux/linuxthreads/LICENSE;md5=73640207fbc79b198c7ffd4ad4d97aa0 \
		"

# Newlib maintainers sometimes publish the source
# from a specific snapshot, after an upgrade we can
# delete the following line and keep the empty default
NEWLIB_SNAPSHOT = ".20211231"
NEWLIB_SNAPSHOT ?= ""
SRC_URI = "https://sourceware.org/pub/newlib/newlib-${PV}${NEWLIB_SNAPSHOT}.tar.gz"
SRC_URI[sha256sum] = "c3a0e8b63bc3bef1aeee4ca3906b53b3b86c8d139867607369cb2915ffc54435"


INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "virtual/${TARGET_PREFIX}gcc"

S = "${WORKDIR}/newlib-${PV}${NEWLIB_SNAPSHOT}"
B = "${WORKDIR}/build"

## disable stdlib
TARGET_CC_ARCH:append = " -nostdlib"

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

do_install() {
	oe_runmake install DESTDIR='${D}'
}

COMPATIBLE_HOST:libc-musl:class-target = "null"
COMPATIBLE_HOST:libc-glibc:class-target = "null"
