DESCRIPTION = "An asynchronous serial port library for Python"
HOMEPAGE = "https://github.com/changyuheng/aioserial.py"
SECTION = "devel/python"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=650b9179efef1ea560df5c08bc32b494"

SRC_URI += "file://0001-use-poetry-core-for-pyproject-base-build.patch"
SRC_URI[sha256sum] = "702bf03b0eb84b8ef2d8dac5cb925e1e685dce98f77b125569bc6fd2b3b58228"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pyserial \
"
