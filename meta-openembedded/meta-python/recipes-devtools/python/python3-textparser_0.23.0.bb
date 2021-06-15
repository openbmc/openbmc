DESCRIPTION = "A text parser written in the Python language."
HOMEPAGE = "https://github.com/eerimoq/textparser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe9942a8bba5458a9dbd11277bc347ad"

SRC_URI[md5sum] = "d5378eac93406156e9029114aaa1b515"
SRC_URI[sha256sum] = "f009d0f6f78aa7c1e648dca4e3961f6d67495f7f520f1a705245ffad33f4b470"

PYPI_PACKAGE = "textparser"

inherit pypi setuptools3

CLEANBROKEN = "1"

