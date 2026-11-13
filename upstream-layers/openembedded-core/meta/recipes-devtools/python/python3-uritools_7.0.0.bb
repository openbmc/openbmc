SUMMARY = "URI parsing, classification and composition"
HOMEPAGE = "https://github.com/tkem/uritools/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=079933dfba36eb60b5e3512ca0ab61ae"

SRC_URI[sha256sum] = "110b773e3f8fbaebcdcbe1377d04e418b1985adf69deeca4ed569f3e328cbf48"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
