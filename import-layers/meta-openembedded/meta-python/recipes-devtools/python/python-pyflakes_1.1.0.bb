SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/dreamhost/cliff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=fe719d45f4c604c6e2c86872b6e9485f"

SRC_URI[md5sum] = "e0bf854cd5abd4527e149692925b82eb"
SRC_URI[sha256sum] = "e5f959931987e2be178781554b485d52342ec9f1b43f891d2dad07a691c7a89a"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    python-prettytable \
    python-cmd2 \
    python-pyparsing"
