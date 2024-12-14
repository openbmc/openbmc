SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb819092901ddb13a7d0a4f5e05f098a"

SRC_URI += " \
            file://0001-pyproject.toml-don-t-pin-dependency-versions.patch \
            file://0001-pyproject.toml-Downgrade-numpy-version-needs-to-1.x.patch \
            "

SRC_URI[sha256sum] = "9e79019aba43cb4fda9e4d983f8e88ca0373adbb697ae9c6c43093218de28b54"

inherit pypi python_mesonpy cython

DEPENDS += " \
    python3-numpy-native \
    python3-versioneer-native \
"

CFLAGS:append:toolchain-clang = " -Wno-error=deprecated-declarations"

RDEPENDS:${PN} += " \
    python3-json \
    python3-numpy \
    python3-dateutil \
    python3-dateutil-zoneinfo \
    python3-pytz \
    python3-profile \
"
