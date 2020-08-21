SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[md5sum] = "d1f258eb70bb916f8fe5535351d5ff05"
SRC_URI[sha256sum] = "40feb7b8b8434785585ab224d1568b857edb18297e5a3047f1ba012bc83b42c1"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
