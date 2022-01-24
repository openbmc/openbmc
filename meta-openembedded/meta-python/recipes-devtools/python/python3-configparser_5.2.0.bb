SUMMARY = "This module provides the ConfigParser class which implements a basic configuration language which provides a structure similar to what's found in Microsoft Windows INI files."
SECTION = "devel/python"
HOMEPAGE = "https://docs.python.org/3/library/configparser.html"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=23f9ad5cad3d8cc0336e2a5d8a87e1fa"

SRC_URI[sha256sum] = "1b35798fdf1713f1c3139016cfcbc461f09edbf099d1fb658d4b7479fcaa3daa"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native ${PYTHON_PN}-toml-native"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
