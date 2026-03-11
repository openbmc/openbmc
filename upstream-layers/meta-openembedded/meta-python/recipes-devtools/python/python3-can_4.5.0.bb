SUMMARY = "Controller Area Network (CAN) interface module for Python"
HOMEPAGE = "https://github.com/hardbyte/python-can"
SECTION = "devel/python"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "d3684cebe5b028a148c1742b3a45cec4fcaf83a7f7c52d0680b2eaeaf52f8eb7"

PYPI_PACKAGE = "python_can"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-aenum \
    python3-asyncio \
    python3-codecs \
    python3-compression \
    python3-ctypes \
    python3-fcntl \
    python3-json \
    python3-logging \
    python3-misc \
    python3-msgpack \
    python3-netserver \
    python3-packaging \
    python3-pkg-resources \
    python3-setuptools \
    python3-sqlite3 \
    python3-typing-extensions \
    python3-wrapt \
"
