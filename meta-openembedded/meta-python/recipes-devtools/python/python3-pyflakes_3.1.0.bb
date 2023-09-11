SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/PyCQA/pyflakes"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=690c2d09203dc9e07c4083fc45ea981f"

SRC_URI[sha256sum] = "a0aae034c444db0071aa077972ba4768d40c830d9539fd45bf4cd3f8f6992efc"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
