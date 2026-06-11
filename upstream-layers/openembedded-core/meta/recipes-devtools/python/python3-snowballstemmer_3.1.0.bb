SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://github.com/snowballstem/snowball"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=07c3b61d971c3df6e19ce439cfe1fb8c"

SRC_URI[sha256sum] = "fd9e34526b23340cd23ffea6c9f9760974ecc2c2ac9e1d81401443ccdb2a801f"

PYPI_PACKAGE = "snowballstemmer"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
