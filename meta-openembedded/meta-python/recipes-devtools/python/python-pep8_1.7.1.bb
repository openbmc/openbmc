SUMMARY = "Python style guide checker"
HOMEPAGE = "https://github.com/dreamhost/cliff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=d8ebbbe831259ce010179d2f310b0f3e"

SRC_URI[md5sum] = "603821d06db945c71d811b5a8d78423c"
SRC_URI[sha256sum] = "fe249b52e20498e59e0b5c5256aa52ee99fc295b26ec9eaa85776ffdb9fe6374"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    python-prettytable \
    python-cmd2 \
    python-pyparsing"

BBCLASSEXTEND = "native nativesdk"
