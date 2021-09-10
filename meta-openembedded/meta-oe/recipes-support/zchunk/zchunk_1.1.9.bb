DESCRIPTION = "A file format designed for highly efficient deltas while maintaining good compression"
AUTHOR = "Jonathan Dieter"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd6e590282010ce90a94ef25dd31410f"

SRC_URI = "git://github.com/zchunk/zchunk.git;protocol=https"

SRCREV = "fe3e3af49fd30b68c21a9fcaac340ad8e7f91055"
S = "${WORKDIR}/git"

DEPENDS = "\
    curl \
    zstd \
    "

DEPENDS:append:libc-musl = " argp-standalone"
LDFLAGS:append:libc-musl = " -largp"

inherit meson

BBCLASSEXTEND = "native nativesdk"
