SUMMARY = "A portable foreign function interface library"
HOMEPAGE = "http://sourceware.org/libffi/"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5e9a8df556846bb3d1edec0d968a2311"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://not-win32.patch \
           "
SRC_URI[sha256sum] = "7da3e2d9a171eb0a038f592ecad3ff2bb2550f3496d87b3b29ad0cf4430c0db4"

EXTRA_OECONF = "--disable-builddir"
EXTRA_OECONF:class-native += "--with-gcc-arch=generic"
EXTRA_OEMAKE:class-target = "LIBTOOLFLAGS='--tag=CC'"

inherit autotools texinfo multilib_header github-releases

do_install:append() {
	oe_multilib_header ffi.h ffitarget.h
}

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
