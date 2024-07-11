SUMMARY = "Library for obtaining the call-chain of a program"
DESCRIPTION = "a portable and efficient C programming interface (API) to determine the call-chain of a program"
HOMEPAGE = "http://www.nongnu.org/libunwind"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d80c8ed4062b8339b715f90fa68cc9f"
DEPENDS += "libatomic-ops"
DEPENDS:append:libc-musl = " libucontext"

SRC_URI = "http://download.savannah.nongnu.org/releases/libunwind/libunwind-${PV}.tar.gz \
           file://mips-byte-order.patch \
           file://mips-coredump-register.patch \
           file://0005-ppc32-Consider-ucontext-mismatches-between-glibc-and.patch \
           file://0001-src-Gtrace-remove-unguarded-print-calls.patch \
           "

SRC_URI[sha256sum] = "4a6aec666991fb45d0889c44aede8ad6eb108071c3554fcdff671f9c94794976"

inherit autotools multilib_header

COMPATIBLE_HOST:riscv32 = "null"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzma] = "--enable-minidebuginfo,--disable-minidebuginfo,xz"
PACKAGECONFIG[zlib] = "--enable-zlibdebuginfo,--disable-zlibdebuginfo,zlib"
PACKAGECONFIG[latexdocs] = "--enable-documentation, --disable-documentation, latex2man-native"

EXTRA_OECONF = "--enable-static"

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

LDFLAGS += "-Wl,-z,relro,-z,now ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"

SECURITY_LDFLAGS:append:libc-musl = " -lssp_nonshared"
CACHED_CONFIGUREVARS:append:libc-musl = " LDFLAGS='${LDFLAGS} -lucontext'"

do_install:append () {
	oe_multilib_header libunwind.h
}

BBCLASSEXTEND = "native"

# http://errors.yoctoproject.org/Errors/Build/183144/
# libunwind-1.6.2/include/tdep-aarch64/libunwind_i.h:123:47: error: passing argument 1 of '_ULaarch64_uc_addr' from incompatible pointer type [-Wincompatible-pointer-types]
# libunwind-1.6.2/src/aarch64/Ginit.c:348:28: error: initialization of 'unw_tdep_context_t *' from incompatible pointer type 'ucontext_t *' [-Wincompatible-pointer-types]
# libunwind-1.6.2/src/aarch64/Ginit.c:377:28: error: initialization of 'unw_tdep_context_t *' from incompatible pointer type 'ucontext_t *' [-Wincompatible-pointer-types]
# libunwind-1.6.2/src/aarch64/Ginit_local.c:51:9: error: assignment to 'ucontext_t *' from incompatible pointer type 'unw_context_t *' {aka 'unw_tdep_context_t *'} [-Wincompatible-pointer-types]
# libunwind-1.6.2/src/aarch64/Gresume.c:37:28: error: initialization of 'unw_tdep_context_t *' from incompatible pointer type 'ucontext_t *' [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
