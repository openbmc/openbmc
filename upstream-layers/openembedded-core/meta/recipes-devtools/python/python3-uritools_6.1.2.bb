SUMMARY = "URI parsing, classification and composition"
HOMEPAGE = "https://github.com/tkem/uritools/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=079933dfba36eb60b5e3512ca0ab61ae"

SRC_URI[sha256sum] = "fa60028843a8be651699a1ee2b399066eeaef349224b32a177efa4aeba463f00"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
