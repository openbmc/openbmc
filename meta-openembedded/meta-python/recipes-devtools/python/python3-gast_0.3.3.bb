SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[md5sum] = "213b1820f576db14ed4fdf57efbfa67f"
SRC_URI[sha256sum] = "b881ef288a49aa81440d2c5eb8aeefd4c2bb8993d5f50edae7413a85bfdb3b57"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
