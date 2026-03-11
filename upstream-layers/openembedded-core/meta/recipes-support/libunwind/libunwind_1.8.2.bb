SUMMARY = "Library for obtaining the call-chain of a program"
DESCRIPTION = "a portable and efficient C programming interface (API) to determine the call-chain of a program"
HOMEPAGE = "http://www.nongnu.org/libunwind"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d80c8ed4062b8339b715f90fa68cc9f"
DEPENDS:append:libc-musl = " libucontext"

GITHUB_BASE_URI = "https://github.com/libunwind/libunwind/releases"
SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BP}.tar.gz \
           file://mips-byte-order.patch \
           file://0001-tests-Garm64-test-sve-signal-check-that-SVE-is-prese.patch \
           file://0002-coredump-use-glibc-or-musl-register-names-as-appropr.patch \
           file://0005-Handle-musl-on-PPC32.patch \
           file://libatomic.patch \
           file://malloc.patch \
           "

SRC_URI[sha256sum] = "7f262f1a1224f437ede0f96a6932b582c8f5421ff207c04e3d9504dfa04c8b82"

inherit autotools multilib_header github-releases

COMPATIBLE_HOST:riscv32 = "null"

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzma] = "--enable-minidebuginfo,--disable-minidebuginfo,xz"
PACKAGECONFIG[zlib] = "--enable-zlibdebuginfo,--disable-zlibdebuginfo,zlib"
PACKAGECONFIG[latexdocs] = "--enable-documentation, --disable-documentation, latex2man-native"

EXTRA_OECONF = "--enable-static --disable-tests"

# http://errors.yoctoproject.org/Errors/Details/20487/
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

# With qemuarm64 and poky-tiny:
# ld: .libs/Gtest-trace: hidden symbol `__aarch64_cas8_acq_rel' in libgcc.a(cas_8_4.o) is referenced by DSO
LDFLAGS_SECTION_REMOVAL = ""

LDFLAGS += "-Wl,-z,relro,-z,now"

SECURITY_LDFLAGS:append:libc-musl = " -lssp_nonshared"
CACHED_CONFIGUREVARS:append:libc-musl = " LDFLAGS='${LDFLAGS} -lucontext'"

do_install:append () {
	oe_multilib_header libunwind.h
}

BBCLASSEXTEND = "native"

# libunwind-1.8.1/src/elfxx.c:205:44: error: passing argument 3 of '_Uppc32_get_func_addr' from incompatible pointer type [-Wincompatible-pointer-types]
# libunwind-1.8.1/src/elfxx.c:279:52: error: passing argument 3 of '_Uppc32_get_func_addr' from incompatible pointer type [-Wincompatible-pointer-types]
# and others
CFLAGS:append:powerpc:libc-musl = " -Wno-error=incompatible-pointer-types"
