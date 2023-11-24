SUMMARY = "Typing stubs for psutil"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=ef4dc1e740f5c928f1608a4a9c7b578e"

inherit pypi setuptools3

SRC_URI[sha256sum] = "f7d8769812d72a4b513d7ec9eb5580fe2f6013fc270394a603cb6534811f3e4d"

BBCLASSEXTEND = "native"
