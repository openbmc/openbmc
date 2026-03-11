SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

SRC_URI[sha256sum] = "77b79ce34a2bdab2594f490c8e80dd62a02d650b91a75159a63ec413b8d104cd"

RDEPENDS:${PN}:class-target += "\
    python3-io \
"

BBCLASSEXTEND = "native nativesdk"
