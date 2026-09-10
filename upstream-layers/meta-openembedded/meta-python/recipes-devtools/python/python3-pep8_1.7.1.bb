DESCRIPTION = "Python style guide checker"
HOMEPAGE = "https://github.com/PyCQA/pycodestyle"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=d8ebbbe831259ce010179d2f310b0f3e"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-prettytable \
            python3-cmd2 \
            python3-pyparsing"

SRC_URI[sha256sum] = "fe249b52e20498e59e0b5c5256aa52ee99fc295b26ec9eaa85776ffdb9fe6374"
