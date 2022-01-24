SUMMARY = "Tools for tracing OpenGL, Direct3D, and other graphics APIs"
HOMEPAGE = "http://apitrace.github.io/"
SECTION = "console/tools"
LICENSE = "MIT & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aeb969185a143c3c25130bc2c3ef9a50 \
                    file://thirdparty/snappy/COPYING;md5=f62f3080324a97b3159a7a7e61812d0c"

SRCREV = "d28a980802ad48568c87da02d630c8babfe163bb"
PV .= "+10.0.1+git${SRCPV}"
SRC_URI = "gitsm://github.com/${BPN}/${BPN}.git;branch=master;protocol=https \
          "

S = "${WORKDIR}/git"

DEPENDS += "zlib libpng python3-native"

inherit cmake

PACKAGECONFIG ??= " ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'x11', '', d)} "
PACKAGECONFIG[x11] = "-DENABLE_X11=ON,-DENABLE_X11=OFF,libx11"

EXTRA_OECMAKE += "\
    -DENABLE_GUI=OFF \
    -DENABLE_STATIC_LIBGCC=OFF \
    -DENABLE_STATIC_LIBSTDCXX=OFF \
    -DPython3_ROOT_DIR=/usr/bin/python3-native \
"

# Use the bundled snappy library
EXTRA_OECMAKE += "\
    -DENABLE_STATIC_SNAPPY=ON \
"

SECURITY_CFLAGS:toolchain-clang = ""
