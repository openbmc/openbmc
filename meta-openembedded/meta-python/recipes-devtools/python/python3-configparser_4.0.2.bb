SUMMARY = "This module provides the ConfigParser class which implements a basic configuration language which provides a structure similar to what's found in Microsoft Windows INI files."
SECTION = "devel/python"
HOMEPAGE = "https://docs.python.org/3/library/configparser.html"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=23f9ad5cad3d8cc0336e2a5d8a87e1fa"

SRC_URI[md5sum] = "35926cc4b9133f1f9ca70a1fd2fdf237"
SRC_URI[sha256sum] = "c7d282687a5308319bf3d2e7706e575c635b0a470342641c93bea0ea3b5331df"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
