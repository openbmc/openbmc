SUMMARY = "OpenGL Mathematics Library for C"
DESCRIPTION = "Highly optimized 2D|3D math library, also known as OpenGL Mathematics (glm) for `C`. \
cglm provides lot of utils to help math operations to be fast and quick to write. It is community \
friendly, feel free to bring any issues, bugs you faced."
HOMEPAGE = "https://github.com/recp/cglm"
BUGTRACKER = "https://github.com/recp/cglm/issues"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8dc95c4110ba741c43832734b51b7de7"

SRC_URI = "git://github.com/recp/cglm;branch=master;protocol=https"
# Tag v0.9.1
SRCREV = "1796cc5ce298235b615dc7a4750b8c3ba56a05dd"

S = "${WORKDIR}/git"

PACKAGECONFIG[build_tests] = "-Dbuild_tests=true,-Dbuild_tests=false,"

PACKAGECONFIG ?= ""

inherit meson pkgconfig

EXTRA_OEMESON += "--buildtype release"

BBCLASSEXTEND = "native"
