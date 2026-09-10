SUMMARY = "URI parsing, classification and composition"
HOMEPAGE = "https://github.com/tkem/uritools/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=079933dfba36eb60b5e3512ca0ab61ae"

SRC_URI[sha256sum] = "3a498e7e85ef3249343d5710618d641a414da0fbae6d23053ada7976ee83ea5f"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

BBCLASSEXTEND = "native nativesdk"
