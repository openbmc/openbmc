SUMMARY = "Seamless operability between C++11 and Python"
HOMEPAGE = "https://github.com/wjakob/pybind11"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=774f65abd8a7fe3124be2cdf766cd06f"

DEPENDS = "boost"

SRC_URI = "git://github.com/pybind/pybind11.git;branch=stable;protocol=https"

SRCREV = "8a099e44b3d5f85b20f05828d919d2332a8de841"

S = "${WORKDIR}/git"

EXTRA_OECMAKE =  "-DPYBIND11_TEST=OFF"

inherit cmake setuptools3 python3native

PIP_INSTALL_DIST_PATH = "${S}/dist"
PIP_INSTALL_PACKAGE = "pybind11"

do_configure() {
   cmake_do_configure
}

do_compile() {
   setuptools3_do_compile
   cmake_do_compile
}

do_install() {
   setuptools3_do_install
   cmake_do_install
}

BBCLASSEXTEND = "native nativesdk"
