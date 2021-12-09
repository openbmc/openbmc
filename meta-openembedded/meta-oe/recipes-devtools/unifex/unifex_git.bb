DESCRIPTION = "C++ Unified Executors library"
HOMEPAGE = "https://github.com/facebookexperimental/libunifex"
SECTION = "libs"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5b86506074cb3cdc493b4f96b73b2909"

SRC_URI = "git://github.com/facebookexperimental/libunifex.git;branch=main;protocol=https"
SRCREV = "9df21c58d34ce8a1cd3b15c3a7347495e29417a0"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGECONFIG ??= "liburing"
PACKAGECONFIG[liburing] = ",,liburing"

EXTRA_OECMAKE += " \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_TESTING=OFF \
    -DCMAKE_CXX_STANDARD=20 \
    -DUNIFEX_BUILD_EXAMPLES=OFF \
    "

BBCLASSEXTEND = "native nativesdk"
