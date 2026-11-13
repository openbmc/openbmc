SUMMARY = "Universal character encoding detector"
HOMEPAGE = "https://github.com/PyYoshi/cChardet"
LICENSE = "MPL-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ecda54f6f525388d71d6b3cd92f7474"

SRC_URI[sha256sum] = "7b062e7e92e5ab1a00aaf5c164d5f056daae648ba1f144082baf5b4c229b794e"

inherit pypi python_setuptools_build_meta cython

BBCLASSEXTEND = "native nativesdk"
