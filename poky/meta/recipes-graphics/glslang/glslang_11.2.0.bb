SUMMARY = "OpenGL / OpenGL ES Reference Compiler"
DESCRIPTION = "Glslang is the official reference compiler front end for the \
OpenGL ES and OpenGL shading languages. It implements a strict interpretation \
of the specifications for these languages. It is open and free for anyone to use, \
either from a command line or programmatically."
SECTION = "graphics"
HOMEPAGE = "https://www.khronos.org/opengles/sdk/tools/Reference-Compiler"
LICENSE = "BSD-3-Clause & BSD-2-Clause & MIT & Apache-2.0 & GPL-3-with-bison-exception"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c5ce49c0456e9b413b98a4368c378229"

SRCREV = "5421877c380d5f92c1965c7a94620dac861297dd"
SRC_URI = "git://github.com/KhronosGroup/glslang.git;protocol=https \
           file://0001-generate-glslang-pkg-config.patch"
UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"
S = "${WORKDIR}/git"

inherit cmake python3native

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=ON \
    -DENABLE_PCH=OFF \
    -DENABLE_CTEST=OFF \
    -DBUILD_EXTERNAL=OFF \
"

SOLIBSDEV = "glslang.so"
# all the other libraries are unversioned, so pack it on PN
FILES_${PN} += "${libdir}/*.so"

BBCLASSEXTEND = "native nativesdk"
