SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT & OpenSSL & BSD-3-Clause & Zlib & ISC & LGPL-2.1-or-later & CC0-1.0 & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=8eacfdc17c8f4d219e131a073973b97d"

DEPENDS = "curl openssl zlib libssh2 libgcrypt libpcre2"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=main;protocol=https \
           file://0001-src-libgit2-CMakeLists.txt-install-cmake-files-into-.patch \
           "
SRCREV = "338e6fb681369ff0537719095e22ce9dc602dbf0"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
    -DBUILD_TESTS=OFF \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DREGEX_BACKEND='pcre2' \
"

BBCLASSEXTEND = "native"

do_install:append() {
    sed -i -e 's,${RECIPE_SYSROOT},,g' ${D}${libdir}/cmake/libgit2/libgit2Targets.cmake
}
