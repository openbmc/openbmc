SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[sha256sum] = "8109cbe7aa0f7bf7e4348379da05b8137ea1f059f073332c3c1cedd57db8541f"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
