SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "ec8b276a6b60bd80defed25add7e439881c19e64850afd9b346283d4165fd0fd"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
