SUMMARY = "URI parsing, classification and composition"
HOMEPAGE = "https://github.com/tkem/uritools/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e21bbe53b2730bfe1911cf381b81821e"

SRC_URI[sha256sum] = "be97e452528e7a42ef0a4df68364ddd77833e982c5bac5cdcfee15c81f65e96b"

inherit pypi python_setuptools_build_meta ptest-python-pytest

BBCLASSEXTEND = "native nativesdk"
