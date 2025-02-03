SUMMARY = "A MutableSet that remembers its order, so that every entry has an index."
HOMEPAGE = "http://github.com/LuminosoInsight/ordered-set"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://MIT-LICENSE;md5=3bf5e1ad64c0d99032c3143361fa234e"

SRC_URI[sha256sum] = "694a8e44c87657c59292ede72891eb91d34131f6531463aab3009191c77364a8"

inherit pypi python_flit_core ptest-python-pytest

PTEST_PYTEST_DIR = "test"

DEPENDS += "python3-pytest-runner-native"

