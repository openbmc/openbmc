SUMMARY = "Snowball compiler and stemming algorithms"
HOMEPAGE = "https://github.com/snowballstem/snowball"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=07c3b61d971c3df6e19ce439cfe1fb8c"

SRC_URI[sha256sum] = "e07bbc54a0d798fe6010a12398422e62a8bfbba95c394fd0956ef58cb4d3e260"

PYPI_PACKAGE = "snowballstemmer"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
