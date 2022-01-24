SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT & openssl"
LIC_FILES_CHKSUM = "file://COPYING;md5=73fa96e40ce64f79bab087c7e1deeacd"

DEPENDS = "curl openssl zlib libssh2 libgcrypt libpcre2"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=main;protocol=https"
SRCREV = "b7bad55e4bb0a285b073ba5e02b01d3f522fc95d"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
    -DBUILD_CLAR=OFF \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DLIB_INSTALL_DIR=${libdir} \
    -DREGEX_BACKEND='pcre2' \
"

BBCLASSEXTEND = "native"
