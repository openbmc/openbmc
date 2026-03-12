SUMMARY = "URI parsing, classification and composition"
HOMEPAGE = "https://github.com/tkem/uritools/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e21bbe53b2730bfe1911cf381b81821e"

SRC_URI[sha256sum] = "2f9e9cb954e7877232b2c863f724a44a06eb98d9c7ebdd69914876e9487b94f8"

inherit pypi python_setuptools_build_meta ptest-python-pytest

BBCLASSEXTEND = "native nativesdk"
