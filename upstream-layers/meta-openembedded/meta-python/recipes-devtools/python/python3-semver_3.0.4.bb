DESCRIPTION = "Python module for Semantic Versioning"
HOMEPAGE = "https://github.com/python-semver/python-semver"
BUGTRACKER = "https://github.com/python-semver/python-semver"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3ce011bad2c5218ddd4973952a6db93a"

SRC_URI[sha256sum] = "afc7d8c584a5ed0a11033af086e8af226a9c0b206f313e0301f8dd7b6b589602"

inherit pypi python_setuptools_build_meta ptest-python-pytest

BBCLASSEXTEND = "native nativesdk"

DEPENDS += " python3-setuptools-scm-native"

