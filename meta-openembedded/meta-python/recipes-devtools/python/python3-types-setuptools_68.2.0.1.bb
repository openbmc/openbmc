SUMMARY = "Typing stubs for setuptools"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=ef4dc1e740f5c928f1608a4a9c7b578e"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8f31e8201e7969789e0eb23463b53ebe5f67d92417df4b648a6ea3c357ca4f51"

BBCLASSEXTEND = "native"
