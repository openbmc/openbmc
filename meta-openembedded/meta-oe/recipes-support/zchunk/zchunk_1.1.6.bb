DESCRIPTION = "A file format designed for highly efficient deltas while maintaining good compression"
AUTHOR = "Jonathan Dieter"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd6e590282010ce90a94ef25dd31410f"

SRC_URI = "git://github.com/zchunk/zchunk.git;protocol=https;branch=master"

SRCREV = "f5593aa11584faa691c81b4898f0aaded47f8bf7"
S = "${WORKDIR}/git"

DEPENDS = "\
    curl \
    zstd \
    "

DEPENDS_append_libc-musl = " argp-standalone"
LDFLAGS_append_libc-musl = " -largp"

inherit meson

BBCLASSEXTEND = "native nativesdk"
