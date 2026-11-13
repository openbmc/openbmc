SUMMARY = "Read, write, repair, and transform PDFs in Python, powered by qpdf"
DESCRIPTION = "A Python library for reading and writing PDF, powered by QPDF"
HOMEPAGE = "https://github.com/pikepdf/pikepdf"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI[sha256sum] = "4b73f926ebae81f04bf14527af330bd00bb268be767e0f189f7c4c3e4ad7ae0a"

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
    install -m 0644 ${S}/LICENSE.txt ${D}${PTEST_PATH}/
    install -d ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/README.md ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/GCC-exception-3.1.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/GPL-3.0.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/libjpeg-turbo-README.ijg.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/libjpeg-turbo.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/microsoft-visual-cpp-runtime.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/openssl.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/qpdf.txt ${D}${PTEST_PATH}/third-party-licenses/
    install -m 0644 ${S}/third-party-licenses/zlib.txt ${D}${PTEST_PATH}/third-party-licenses/
}

BBCLASSEXTEND = "native nativesdk"
