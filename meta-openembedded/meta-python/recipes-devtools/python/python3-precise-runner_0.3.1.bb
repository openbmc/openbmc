SUMMARY = "A lightweight, simple-to-use, RNN wake word listener."
HOMEPAGE = "https://github.com/MycroftAI/mycroft-precise"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=2ad29e930f3107d52d2a55728bf62116"

SRC_URI[sha256sum] = "1a464209fb4bf0a3f5d5a428310cb2a70487a01a6bc3a960d1dda90af896b80d"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "test"
