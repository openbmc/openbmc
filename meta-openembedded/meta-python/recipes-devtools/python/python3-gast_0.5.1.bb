SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[sha256sum] = "b00e63584db482ffe6107b5832042bbe5c5bf856e3c7279b6e93201b3dcfcb46"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
