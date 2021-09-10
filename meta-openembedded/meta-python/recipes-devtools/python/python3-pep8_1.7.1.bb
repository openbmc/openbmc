DESCRIPTION = "Python style guide checker"
HOMEPAGE = "https://github.com/PyCQA/pycodestyle"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=d8ebbbe831259ce010179d2f310b0f3e"

SRC_URI[md5sum] = "a03bb494859e87b42601b61b1b043a0c"
SRC_URI[sha256sum] = "603a46e5c358ce20ac4807a0eeafac7505d1125a4c1bd8378757ada06f61bed8"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-prettytable \
            ${PYTHON_PN}-cmd2 \
            ${PYTHON_PN}-pyparsing"

SRC_URI[md5sum] = "603821d06db945c71d811b5a8d78423c"
SRC_URI[sha256sum] = "fe249b52e20498e59e0b5c5256aa52ee99fc295b26ec9eaa85776ffdb9fe6374"
