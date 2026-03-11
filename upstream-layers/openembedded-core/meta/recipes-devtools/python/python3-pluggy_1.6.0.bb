SUMMARY = "Plugin and hook calling mechanisms for python"
HOMEPAGE = "https://github.com/pytest-dev/pluggy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c8206d16fd5cc02fa9b0bb98955e5c2"

SRC_URI[sha256sum] = "7dcc130b76258d33b90f61b658791dede3486c3e6bfb003ee5c9bfb396dd22f3"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi ptest-python-pytest python_setuptools_build_meta

PTEST_PYTEST_DIR = "testing"

BBCLASSEXTEND = "native nativesdk"
