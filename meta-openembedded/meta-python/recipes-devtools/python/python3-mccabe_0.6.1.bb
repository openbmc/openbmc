DESCRIPTION = "McCabe checker, plugin for flake8"
HOMEPAGE = "https://github.com/dreamhost/cliff"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a489dc62bacbdad3335c0f160a974f0f"

SRC_URI[md5sum] = "723df2f7b1737b8887475bac4c763e1e"
SRC_URI[sha256sum] = "dd8d182285a0fe56bace7f45b5e7d1a6ebcbf524e8f3bd87eb0f125271b8831f"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

RDEPENDS_${PN} += "${PYTHON_PN}-prettytable \
            ${PYTHON_PN}-cmd2 \
            ${PYTHON_PN}-pyparsing \
           "
