SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/dreamhost/cliff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=eb48916870306ef34a061cec727ccae5"

SRC_URI[md5sum] = "995747589e97c75055cf5b4e1e031e0b"
SRC_URI[sha256sum] = "2e4a1b636d8809d8f0a69f341acf15b2e401a3221ede11be439911d23ce2139e"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    python-prettytable \
    python-cmd2 \
    python-pyparsing"
