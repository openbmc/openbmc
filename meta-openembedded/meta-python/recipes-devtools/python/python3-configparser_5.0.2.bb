SUMMARY = "This module provides the ConfigParser class which implements a basic configuration language which provides a structure similar to what's found in Microsoft Windows INI files."
SECTION = "devel/python"
HOMEPAGE = "https://docs.python.org/3/library/configparser.html"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=23f9ad5cad3d8cc0336e2a5d8a87e1fa"

SRC_URI[sha256sum] = "85d5de102cfe6d14a5172676f09d19c465ce63d6019cf0a4ef13385fc535e828"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native ${PYTHON_PN}-toml-native"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
