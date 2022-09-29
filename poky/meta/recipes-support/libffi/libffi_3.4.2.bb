SUMMARY = "A portable foreign function interface library"
HOMEPAGE = "http://sourceware.org/libffi/"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=679b5c9bdc79a2b93ee574e193e7a7bc"

SRC_URI = "https://github.com/libffi/libffi/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://not-win32.patch \
           file://0001-arm-sysv-reverted-clang-VFP-mitigation.patch \
           "
SRC_URI[sha256sum] = "540fb721619a6aba3bdeef7d940d8e9e0e6d2c193595bc243241b77ff9e93620"
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
