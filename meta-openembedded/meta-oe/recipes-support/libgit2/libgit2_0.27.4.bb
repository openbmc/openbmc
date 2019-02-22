SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f2cd5d3cccd71d62066ba619614592b"

DEPENDS = "curl openssl zlib libssh2 libgcrypt"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=maint/v0.27"
#v0.27.5
SRCREV = "8e0b172981a046d19f1d9efa5acd6186bccbd3ce"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
    -DTHREADSAFE=ON \
    -DBUILD_CLAR=OFF \
    -DSHA1_TYPE="builtin" \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
    -DBUILD_EXAMPLES=OFF \
    -DLIB_INSTALL_DIR=${libdir} \
"

BBCLASSEXTEND = "native"
