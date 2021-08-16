SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[sha256sum] = "f81fcefa8b982624a31c9e4ec7761325a88a0eba60d36d1da90e47f8fe3c67f7"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
