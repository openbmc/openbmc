SUMMARY = "A cross-platform process and system utilities module for Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a9c72113a843d0d732a0ac1c200d81b1"
HOMEPAGE = "https://pypi.org/project/psutil/"

SRC_URI[sha256sum] = "cf8496728c18f2d0b45198f06895be52f36611711746b7f30c464b422b50e2f5"

inherit pypi python_setuptools_build_meta

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/test* \
    ${PYTHON_SITEPACKAGES_DIR}/psutil/__pycache__/test* \
"


RDEPENDS:${PN} += " \
    python3-shell \
    python3-threading \
    python3-xml \
    python3-netclient \
    python3-ctypes \
    python3-resource \
"

RDEPENDS:${PN}-tests += " \
     ${PN} \
     python3 \
     coreutils \
     procps \
     binutils \
     gcc \
     gcc-symlinks \
     libstdc++ \
     libstdc++-dev \
"
RDEPENDS:${PN}-tests:class-native = ""

INSANE_SKIP:${PN}-tests += "dev-deps"

CVE_PRODUCT = "psutil"

BBCLASSEXTEND = "native"
