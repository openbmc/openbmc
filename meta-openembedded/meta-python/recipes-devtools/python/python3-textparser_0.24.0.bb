DESCRIPTION = "A text parser written in the Python language."
HOMEPAGE = "https://github.com/eerimoq/textparser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe9942a8bba5458a9dbd11277bc347ad"

SRC_URI[sha256sum] = "56f708e75aa9d002adb76d823ba6ef166d7ecec1e3e4ca4c1ca103f817568335"

PYPI_PACKAGE = "textparser"

inherit pypi setuptools3

CLEANBROKEN = "1"

