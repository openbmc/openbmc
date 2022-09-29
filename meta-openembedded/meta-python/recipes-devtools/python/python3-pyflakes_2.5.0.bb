SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "491feb020dca48ccc562a8c0cbe8df07ee13078df59813b83959cbdada312ea3"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
