SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

SRC_URI[sha256sum] = "04c721c2c7448767e9e3f2520a475663d8ee0f09c31890f6d2bd70fd636a9647"

RDEPENDS:${PN}:append:class-target = " \
    python3-io \
"

BBCLASSEXTEND = "native nativesdk"
