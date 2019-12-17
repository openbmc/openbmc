SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f2cd5d3cccd71d62066ba619614592b"

DEPENDS = "curl openssl zlib libssh2 libgcrypt"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=maint/v0.28"
SRCREV = "7ce88e66a19e3b48340abcdd86aeaae1882e63cc"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
    -DBUILD_CLAR=OFF \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
"

BBCLASSEXTEND = "native"
