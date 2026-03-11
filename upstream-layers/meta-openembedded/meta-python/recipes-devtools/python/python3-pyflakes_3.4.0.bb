SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "b24f96fafb7d2ab0ec5075b7350b3d2d2218eab42003821c06344973d3ea2f58"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
