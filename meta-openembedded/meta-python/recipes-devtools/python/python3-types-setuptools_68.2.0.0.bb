SUMMARY = "Typing stubs for setuptools"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=ef4dc1e740f5c928f1608a4a9c7b578e"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a4216f1e2ef29d089877b3af3ab2acf489eb869ccaf905125c69d2dc3932fd85"

BBCLASSEXTEND = "native"
