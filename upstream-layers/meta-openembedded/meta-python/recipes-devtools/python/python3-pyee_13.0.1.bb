SUMMARY = "A rough port of Node.js's EventEmitter to Python with a few tricks of its own"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2b1cc8797dff32cec5d783148fceab5"

DEPENDS = "python3-setuptools-scm-native"
SRC_URI[sha256sum] = "0b931f7c14535667ed4c7e0d531716368715e860b988770fc7eb8578d1f67fc8"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += "python3-typing-extensions"

PYPI_PACKAGE = "pyee"
