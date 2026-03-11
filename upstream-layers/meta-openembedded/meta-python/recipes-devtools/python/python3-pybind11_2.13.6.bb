SUMMARY = "Seamless operability between C++11 and Python"
HOMEPAGE = "https://github.com/pybind/pybind11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=774f65abd8a7fe3124be2cdf766cd06f"
DEPENDS = "\
    python3-cmake-native \
    python3-ninja-native \
"

SRCREV = "a2e59f0e7065404b44dfe92a28aca47ba1378dc4"
SRC_URI = "\
    git://github.com/pybind/pybind11.git;branch=stable;protocol=https \
"


inherit cmake python_setuptools_build_meta

EXTRA_OECMAKE = "-DPYBIND11_TEST=OFF -DPYBIND11_USE_CROSSCOMPILING=ON"

do_configure:append() {
    cmake_do_configure
}

do_compile:append() {
    cmake_do_compile
}

do_install:append() {
    cmake_do_install
}

BBCLASSEXTEND = "native nativesdk"
