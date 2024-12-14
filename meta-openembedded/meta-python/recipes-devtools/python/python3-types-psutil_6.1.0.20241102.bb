SUMMARY = "Typing stubs for psutil"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=c2d9643b4523fdf462545aeb1356ad23"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8cbe086b9c29f5c0aa55c4422498c07a8e506f096205761dba088905198551dc"

BBCLASSEXTEND = "native"
