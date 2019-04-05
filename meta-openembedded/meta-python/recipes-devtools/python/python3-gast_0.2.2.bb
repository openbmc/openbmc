SUMMARY = "A generic AST to represent Python2 and Python3's Abstract Syntax Tree(AST)."
HOMEPAGE = "https://github.com/serge-sans-paille/gast"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3ad9b6802e713fc5e307e1230f1ea90"

SRC_URI = "git://github.com/serge-sans-paille/gast.git"
SRCREV ?= "ed82e2a507505c6b18eb665d3738b6c0602da5e7"

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
