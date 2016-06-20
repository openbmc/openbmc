SUMMARY = "Library for solving packages and reading repositories"
HOMEPAGE = "https://github.com/openSUSE/libsolv"
BUGTRACKER = "https://github.com/openSUSE/libsolv/issues"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.BSD;md5=62272bd11c97396d4aaf1c41bc11f7d8"

DEPENDS = "expat zlib"

PV = "0.6.19"

SRC_URI = "git://github.com/openSUSE/libsolv.git \
           file://0001-CMakeLists.txt-fix-MAN_INSTALL_DIR.patch \
"
SRCREV = "4c5af401a89858d4cebbfe40c59a0031ff9db5b0"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DLIB=${baselib}"

PACKAGES =+ "${PN}-tools ${PN}ext"

FILES_${PN}-dev += "${datadir}/cmake/Modules/FindLibSolv.cmake"
FILES_${PN}-tools = "${bindir}/*"
FILES_${PN}ext = "${libdir}/${PN}ext.so.*"

BBCLASSEXTEND =+ "native nativesdk"
