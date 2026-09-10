SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

SRC_URI[sha256sum] = "9eb0b0e602064527a045ea28c4f174ed69383587e29cebe28947e3b84106eb2a"

RDEPENDS:${PN}:append:class-target = " \
    python3-io \
"

# python3-misc for tracemalloc (used by test/test_except.py)
RDEPENDS:${PN}-ptest += " \
    python3-misc \
"

BBCLASSEXTEND = "native nativesdk"
