SUMMARY = "A portable foreign function interface library"
HOMEPAGE = "http://sourceware.org/libffi/"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32c0d09a0641daf4903e5d61cc8f23a8"

SRC_URI = "https://github.com/libffi/libffi/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://not-win32.patch \
           file://0001-arm-sysv-reverted-clang-VFP-mitigation.patch \
           "
SRC_URI[sha256sum] = "d66c56ad259a82cf2a9dfc408b32bf5da52371500b84745f7fb8b645712df676"
UPSTREAM_CHECK_URI = "https://github.com/libffi/libffi/releases/"
UPSTREAM_CHECK_REGEX = "libffi-(?P<pver>\d+(\.\d+)+)\.tar"

EXTRA_OECONF += "--disable-builddir --disable-exec-static-tramp"
EXTRA_OECONF:class-native += "--with-gcc-arch=generic"
EXTRA_OEMAKE:class-target = "LIBTOOLFLAGS='--tag=CC'"
inherit autotools texinfo multilib_header

do_install:append() {
	oe_multilib_header ffi.h ffitarget.h
}

FILES:${PN}-dev += "${libdir}/libffi-${PV}"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
