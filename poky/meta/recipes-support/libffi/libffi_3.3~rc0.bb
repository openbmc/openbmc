SUMMARY = "A portable foreign function interface library"
HOMEPAGE = "http://sourceware.org/libffi/"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3610bb17683a0089ed64055416b2ae1b"

SRC_URI = "https://github.com/libffi/libffi/releases/download/v3.3-rc0/libffi-3.3-rc0.tar.gz \
           file://not-win32.patch \
           file://0001-Fixed-missed-ifndef-for-__mips_soft_float.patch \
           "
SRC_URI[md5sum] = "8d2a82a78faf10a5e53c27d986e8f04e"
SRC_URI[sha256sum] = "403d67aabf1c05157855ea2b1d9950263fb6316536c8c333f5b9ab1eb2f20ecf"
UPSTREAM_CHECK_URI = "https://github.com/libffi/libffi/releases/"
UPSTREAM_CHECK_REGEX = "libffi-(?P<pver>\d+(\.\d+)+)\.tar"
UPSTREAM_VERSION_UNKNOWN = "1"

EXTRA_OECONF += "--disable-builddir"
EXTRA_OEMAKE_class-target = "LIBTOOLFLAGS='--tag=CC'"
inherit autotools texinfo multilib_header

S = "${WORKDIR}/${BPN}-3.3-rc0"

do_install_append() {
	oe_multilib_header ffi.h
}

FILES_${PN}-dev += "${libdir}/libffi-${PV}"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"

