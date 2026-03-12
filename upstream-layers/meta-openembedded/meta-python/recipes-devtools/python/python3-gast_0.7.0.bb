SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI[sha256sum] = "0bb14cd1b806722e91ddbab6fb86bba148c22b40e7ff11e248974e04c8adfdae"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
