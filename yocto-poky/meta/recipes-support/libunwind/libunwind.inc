SUMMARY = "Library for obtaining the call-chain of a program"
DESCRIPTION = "a portable and efficient C programming interface (API) to determine the call-chain of a program"
HOMEPAGE = "http://www.nongnu.org/libunwind"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3fced11d6df719b47505837a51c16ae5"
DEPENDS += "libatomic-ops"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzma] = "--enable-minidebuginfo,--disable-minidebuginfo,xz"

EXTRA_OECONF_arm = "--enable-debug-frame"
EXTRA_OECONF_aarch64 = "--enable-debug-frame"

CFLAGS += "${ATOMICOPS}"
ATOMICOPS_armv5 = "-DAO_USE_PTHREAD_DEFS=1"
ATOMICOPS_armv4 = "-DAO_USE_PTHREAD_DEFS=1"
ATOMICOPS ?= ""

LDFLAGS_append_x86 = " -fuse-ld=gold"
LDFLAGS_append_x86-64 = " -fuse-ld=gold"

LDFLAGS_append_arm = " -fuse-ld=gold"
LDFLAGS_append_aarch64 = " -fuse-ld=gold"

LDFLAGS_append_powerpc = " -fuse-ld=gold -mbss-plt"
LDFLAGS_append_powerpc64 = " -fuse-ld=gold -mbss-plt"

SECURITY_LDFLAGS_append_libc-musl = " -lssp_nonshared -lssp"

BBCLASSEXTEND = "native"
