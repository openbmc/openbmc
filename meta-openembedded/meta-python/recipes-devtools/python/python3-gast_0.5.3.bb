SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[sha256sum] = "cfbea25820e653af9c7d1807f659ce0a0a9c64f2439421a7bba4f0983f532dea"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
