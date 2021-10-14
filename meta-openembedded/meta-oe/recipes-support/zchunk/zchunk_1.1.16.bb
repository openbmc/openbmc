DESCRIPTION = "A file format designed for highly efficient deltas while maintaining good compression"
AUTHOR = "Jonathan Dieter"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=daf6e68539f564601a5a5869c31e5242"

SRC_URI = "git://github.com/zchunk/zchunk.git;protocol=https;branch=main"

SRCREV = "ff34ee911f7d78d66b97894a773276babd63144c"
S = "${WORKDIR}/git"

DEPENDS = "\
    curl \
    zstd \
    "

DEPENDS:append:libc-musl = " argp-standalone"
LDFLAGS:append:libc-musl = " -largp"

inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"
