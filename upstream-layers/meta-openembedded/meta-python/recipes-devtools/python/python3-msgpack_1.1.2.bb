SUMMARY = "MessagePack (de)serializer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=cd9523181d9d4fbf7ffca52eaa2a5751"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

SRC_URI[sha256sum] = "3b60763c1373dd60f398488069bcdc703cd08a711477b5d480eecc9f9626f47e"

RDEPENDS:${PN}:class-target += "\
    python3-io \
"

BBCLASSEXTEND = "native nativesdk"
