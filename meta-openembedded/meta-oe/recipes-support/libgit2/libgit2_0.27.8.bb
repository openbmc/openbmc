SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f2cd5d3cccd71d62066ba619614592b"

DEPENDS = "curl openssl zlib libssh2 libgcrypt"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=maint/v0.27"
#v0.27.8
SRCREV = "2882803ca2b2cdedd069746422155a23fdd002b2"

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
