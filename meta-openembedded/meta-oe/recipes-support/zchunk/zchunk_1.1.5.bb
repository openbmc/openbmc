DESCRIPTION = "A file format designed for highly efficient deltas while maintaining good compression"
AUTHOR = "Jonathan Dieter"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd6e590282010ce90a94ef25dd31410f"

SRC_URI = " \
	git://github.com/zchunk/zchunk.git;protocol=https \
	file://0001-zck.h-fix-build-on-musl.patch \
	file://0002-unzck-fix-build-with-musl-libc.patch \
	"

SRCREV = "c01bf12feede792982f165f52f4a6c573e3a8c17"
S = "${WORKDIR}/git"

DEPENDS = "\
    curl \
    zstd \
    "

DEPENDS_append_libc-musl = " argp-standalone"
LDFLAGS_append_libc-musl = " -largp"

inherit meson

BBCLASSEXTEND = "native nativesdk"
