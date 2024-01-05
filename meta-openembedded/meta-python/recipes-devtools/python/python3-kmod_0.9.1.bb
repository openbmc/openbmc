SUMMARY = "Python bindings for kmod/libkmod."
HOMEPAGE = "https://github.com/agrover/python-kmod"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d62c2454850386a2ffe44f72db83d74"

inherit pypi setuptools3

RECIPE_NO_UPDATE_REASON = "Version 0.9.2 is broken when using latest build machinery for C extensions"
SRC_URI[sha256sum] = "f3bf829059bf88eca22f4f549e17aa316cdaa14302bf2ba49ddeee60cea109ff"

DEPENDS += " \
    kmod \
    python3-cython-native \
"
