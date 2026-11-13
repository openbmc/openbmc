SUMMARY = "A rough port of Node.js's EventEmitter to Python with a few tricks of its own"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2b1cc8797dff32cec5d783148fceab5"

DEPENDS = "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "34de6fbc4d8f0880bb997efd0eb642ba01f02688e740cbd3aa3bee9db4b1c5ec"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} += "python3-typing-extensions"
