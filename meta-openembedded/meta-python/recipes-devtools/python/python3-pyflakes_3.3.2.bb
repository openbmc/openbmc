SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "6dfd61d87b97fba5dcfaaf781171ac16be16453be6d816147989e7f6e6a9576b"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
