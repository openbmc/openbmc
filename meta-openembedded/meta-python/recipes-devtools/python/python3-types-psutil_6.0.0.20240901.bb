SUMMARY = "Typing stubs for psutil"
HOMEPAGE = "https://github.com/python/typeshed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=c2d9643b4523fdf462545aeb1356ad23"

inherit pypi setuptools3

SRC_URI[sha256sum] = "437affa76670363db9ffecfa4f153cc6900bf8a7072b3420f3bc07a593f92226"

BBCLASSEXTEND = "native"
