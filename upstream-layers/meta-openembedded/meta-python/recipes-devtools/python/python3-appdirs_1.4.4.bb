SUMMARY = "A small Python module for determining appropriate + platform-specific dirs, e.g. a user data dir."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=31625363c45eb0c67c630a2f73e438e4"

SRC_URI[sha256sum] = "7d5d0167b2b1ba821647616af46a749d1c653740dd0d2415100fe26e27afdf41"

inherit pypi setuptools3 ptest-python-pytest

PTEST_PYTEST_DIR = "test"

BBCLASSEXTEND = "native nativesdk"
