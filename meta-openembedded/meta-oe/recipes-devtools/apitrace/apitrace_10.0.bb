SUMMARY = "Tools for tracing OpenGL, Direct3D, and other graphics APIs"
HOMEPAGE = "http://apitrace.github.io/"
SECTION = "console/tools"
LICENSE = "MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aeb969185a143c3c25130bc2c3ef9a50 \
                    file://thirdparty/snappy/COPYING;md5=f62f3080324a97b3159a7a7e61812d0c"

SRCREV = "9d42f667e2a36a6624d92b9bd697de097cc4e619"
PV .= "+10.0.1+git${SRCPV}"
SRC_URI = "gitsm://github.com/${BPN}/${BPN}.git \
           file://0001-Workaround-glibc-2.34-build-failure-by-disabling-dls.patch \
          "

S = "${WORKDIR}/git"

DEPENDS += "zlib libpng python3-native"

inherit cmake

EXTRA_OECMAKE += "\
    -DENABLE_GUI=OFF \
    -DENABLE_X11=OFF \
    -DENABLE_STATIC_LIBGCC=OFF \
    -DENABLE_STATIC_LIBSTDCXX=OFF \
    -DPython3_ROOT_DIR=/usr/bin/python3-native \
"

# Use the bundled snappy library
EXTRA_OECMAKE += "\
    -DENABLE_STATIC_SNAPPY=ON \
"

SECURITY_CFLAGS:toolchain-clang = ""
