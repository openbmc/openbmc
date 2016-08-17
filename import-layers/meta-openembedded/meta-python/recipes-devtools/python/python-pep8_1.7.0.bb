SUMMARY = "Python style guide checker"
HOMEPAGE = "https://github.com/dreamhost/cliff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=d8ebbbe831259ce010179d2f310b0f3e"

SRC_URI[md5sum] = "2b03109b0618afe3b04b3e63b334ac9d"
SRC_URI[sha256sum] = "a113d5f5ad7a7abacef9df5ec3f2af23a20a28005921577b15dd584d099d5900"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    python-prettytable \
    python-cmd2 \
    python-pyparsing"
