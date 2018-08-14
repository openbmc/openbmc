SUMMARY = "the Git linkable library"
HOMEPAGE = "http://libgit2.github.com/"
LICENSE = "GPL-2.0-with-GCC-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=34197a479f637beb9e09e56893f48bc2"

DEPENDS = "curl openssl zlib libssh2"

SRC_URI = "git://github.com/libgit2/libgit2.git;branch=maint/v0.24"
SRCREV = "4cf1ec7cff28da8838a2f0a9fb330e312ea3f963"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "\
        -DTHREADSAFE=ON \
        -DBUILD_CLAR=OFF \
        -DSHA1_TYPE="builtin" \
        -DCMAKE_BUILD_TYPE=RelWithDebInfo \
        -DBUILD_EXAMPLES=OFF \
"

BBCLASSEXTEND = "native"
