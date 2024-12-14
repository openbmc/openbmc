SUMMARY = "Controller Area Network (CAN) interface module for Python"
HOMEPAGE = "https://github.com/hardbyte/python-can"
SECTION = "devel/python"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "1c46c0935f39f7a9c3e76b03249af0580689ebf7a1844195e92f87257f009df5"

PYPI_PACKAGE = "python_can"

inherit pypi python_setuptools_build_meta

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
    python3-netserver \
    python3-packaging \
    python3-pkg-resources \
    python3-setuptools \
    python3-sqlite3 \
    python3-typing-extensions \
    python3-wrapt \
"
