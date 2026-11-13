SUMMARY = "Plugin and hook calling mechanisms for python"
HOMEPAGE = "https://github.com/pytest-dev/pluggy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c8206d16fd5cc02fa9b0bb98955e5c2"

SRC_URI[sha256sum] = "d1eaa46ebb595891b860ab086b4d09c8588af65ebd4361b8e8f4bb8920b90ba8"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi ptest-python-pytest python_setuptools_build_meta

PTEST_PYTEST_DIR = "testing"

BBCLASSEXTEND = "native nativesdk"
