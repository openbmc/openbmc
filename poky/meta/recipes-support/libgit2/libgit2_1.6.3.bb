SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT & OpenSSL & BSD-3-Clause & Zlib & ISC & LGPL-2.1-or-later & CC0-1.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=5bdf47bbc9a39dc6ce076d59e322dc17"

DEPENDS = "curl openssl zlib libssh2 libgcrypt libpcre2"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=maint/v1.6;protocol=https"
SRCREV = "c058aa87dce4c67a3b86b3349beebd64b7bedcd3"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
    -DBUILD_TESTS=OFF \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DREGEX_BACKEND='pcre2' \
"

BBCLASSEXTEND = "native"
