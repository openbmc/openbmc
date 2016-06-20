SUMMARY = "A portable foreign function interface library"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3610bb17683a0089ed64055416b2ae1b"

SRC_URI = "ftp://sourceware.org/pub/libffi/${BP}.tar.gz \
           file://not-win32.patch \
	   file://0001-mips-Use-compiler-internal-define-for-linux.patch \
	   "

SRC_URI[md5sum] = "83b89587607e3eb65c70d361f13bab43"
SRC_URI[sha256sum] = "d06ebb8e1d9a22d19e38d63fdb83954253f39bedc5d46232a05645685722ca37"

EXTRA_OECONF += "--disable-builddir"

inherit autotools texinfo

FILES_${PN}-dev += "${libdir}/libffi-${PV}"

BBCLASSEXTEND = "native nativesdk"
