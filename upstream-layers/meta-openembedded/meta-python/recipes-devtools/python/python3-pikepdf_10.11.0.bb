SUMMARY = "Read, write, repair, and transform PDFs in Python, powered by qpdf"
DESCRIPTION = "A Python library for reading and writing PDF, powered by QPDF"
HOMEPAGE = "https://github.com/pikepdf/pikepdf"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI[sha256sum] = "4bebc59ac74411064e7c97a4036174023e22ccbecd22b78750ab76551eb38988"

SRC_URI += "file://0001-pyproject.toml-Do-not-strip.patch"

inherit pypi python_setuptools_build_meta ptest-python-pytest

# pikepdf uses the C++20 language but no C++20 named modules. CMake's module
# dependency scanning (clang-scan-deps) does not work in the cross environment,
# so turn it off.
export CMAKE_ARGS = "-DCMAKE_CXX_SCAN_FOR_MODULES=OFF"


CVE_PRODUCT = "pikepdf"

DEPENDS += " \
    python3-pybind11-native \
    python3-nanobind-native \
    python3-scikit-build-core-native \
    ninja-native \
    qpdf \
"

RDEPENDS:${PN} += " \
    qpdf \
    python3-pillow \
    python3-lxml \
"

RDEPENDS:${PN}-ptest += " \
    python3-attrs \
    python3-hypothesis \
    python3-numpy \
    python3-pytest-cov \
    python3-pytest-timeout \
    python3-pytest-xdist \
    python3-psutil \
    python3-dateutil \
    python3-python-xmp-toolkit \
    python3-tomli \
"

do_install_ptest:append() {
    install -m 0644 ${S}/pyproject.toml ${D}${PTEST_PATH}/
}

BBCLASSEXTEND = "native nativesdk"
