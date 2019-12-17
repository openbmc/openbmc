SUMMARY = "A port of Python 3.4 statistics module to Python 2.x"
DESCRIPTION = " \
A port of Python 3.4 statistics module to Python 2.*, initially done \
through the 3to2 tool. This module provides functions for calculating \
mathematical statistics of numeric (Real-valued) data. \
"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://statistics/__init__.py;beginline=6;endline=16;md5=b76960ee777a1529f60db47ada2a8e6e"

SRC_URI[md5sum] = "d6d97f3a1a7b3192cff99e0f2b5956c3"
SRC_URI[sha256sum] = "2dc379b80b07bf2ddd5488cad06b2b9531da4dd31edb04dc9ec0dc226486c138"

inherit pypi setuptools

PYPI_PACKAGE = "statistics"
