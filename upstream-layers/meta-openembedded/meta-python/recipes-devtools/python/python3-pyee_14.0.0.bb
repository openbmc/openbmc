SUMMARY = "A rough port of Node.js's EventEmitter to Python with a few tricks of its own"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2b1cc8797dff32cec5d783148fceab5"

DEPENDS = "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "76dd0f4314ecd27f02dc73589dea7fd3853f9b6176d8ef9b122860657e3602de"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += "python3-typing-extensions"
