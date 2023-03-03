DESCRIPTION = "A file format designed for highly efficient deltas while maintaining good compression"
AUTHOR = "Jonathan Dieter"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=daf6e68539f564601a5a5869c31e5242"

SRC_URI = "git://github.com/zchunk/zchunk.git;protocol=https;branch=main"

SRCREV = "1d8f14484b4e0b507487abe6c294fb2059b7b72f"
S = "${WORKDIR}/git"

DEPENDS = "zstd"
DEPENDS:append:libc-musl = " argp-standalone"

inherit meson pkgconfig lib_package

PACKAGECONFIG ??= "openssl zckdl"

# zckdl gets packaged into zchunk-bin
PACKAGECONFIG[zckdl] = "-Dwith-curl=enabled,-Dwith-curl=disabled,curl"
# Use OpenSSL primitives for SHA
PACKAGECONFIG[openssl] = "-Dwith-openssl=enabled,-Dwith-openssl=disabled,openssl"

BBCLASSEXTEND = "native nativesdk"
