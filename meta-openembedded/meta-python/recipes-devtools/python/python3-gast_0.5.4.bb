SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[sha256sum] = "9c270fe5f4b130969b54174de7db4e764b09b4f7f67ccfc32480e29f78348d97"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
