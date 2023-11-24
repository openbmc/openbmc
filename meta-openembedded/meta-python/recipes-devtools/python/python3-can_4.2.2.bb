SUMMARY = "Controller Area Network (CAN) interface module for Python"
SECTION = "devel/python"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "6ad50f4613289f3c4d276b6d2ac8901d776dcb929994cce93f55a69e858c595f"

PYPI_PACKAGE="python-can"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
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
