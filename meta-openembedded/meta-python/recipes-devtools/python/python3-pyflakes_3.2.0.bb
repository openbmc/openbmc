SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "1c61603ff154621fb2a9172037d84dca3500def8c8b630657d1701f026f8af3f"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
