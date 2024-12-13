SUMMARY = "Typing stubs for setuptools"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=c2d9643b4523fdf462545aeb1356ad23"

inherit pypi setuptools3

SRC_URI[sha256sum] = "3a060681098eb3fbc2fea0a86f7f6af6aa1ca71906039d88d891ea2cecdd4dbf"

BBCLASSEXTEND = "native"
