SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

SRC_URI[sha256sum] = "475c44e12f2a5de766a8448783f149c9b33f3f49b88953c6cd739a77d93c732c"

RDEPENDS:${PN}:append:class-target = " \
    python3-io \
"

# python3-misc for tracemalloc (used by test/test_except.py)
RDEPENDS:${PN}-ptest += " \
    python3-misc \
"

BBCLASSEXTEND = "native nativesdk"
