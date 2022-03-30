SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT & OpenSSL"
LIC_FILES_CHKSUM = "file://COPYING;md5=e5a9227de4cb6afb5d35ed7b0fdf480d"

DEPENDS = "curl openssl zlib libssh2 libgcrypt libpcre2"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=maint/v1.4;protocol=https"
SRCREV = "182d0d1ee933de46bf0b5a6ec269bafa77aba9a2"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
    -DBUILD_CLAR=OFF \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DLIB_INSTALL_DIR=${libdir} \
    -DREGEX_BACKEND='pcre2' \
"

BBCLASSEXTEND = "native"
