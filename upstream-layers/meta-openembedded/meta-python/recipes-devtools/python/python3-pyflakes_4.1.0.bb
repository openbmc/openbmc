SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "592beb57058eb82eb190fb7514f2081c555c51000b029ff71286a681e6cf2f83"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
