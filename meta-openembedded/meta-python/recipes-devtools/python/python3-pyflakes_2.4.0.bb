SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "05a85c2872edf37a4ed30b0cce2f6093e1d0581f8c19d7393122da7e25b2b24c"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
