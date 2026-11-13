SUMMARY = "A specification that python filesystems should adhere to."
HOMEPAGE = "https://github.com/fsspec/filesystem_spec"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b38a11bf4dcdfc66307f8515ce1fbaa6"

SRC_URI = "git://github.com/fsspec/filesystem_spec;protocol=https;branch=master;tag=${PV}"
SRCREV = "0f76baaae4a227b085b18e8469e6cc1792a95ade"

SRC_URI += "file://run-ptest"

inherit python_hatchling ptest-python-pytest

DEPENDS = "python3-hatch-vcs-native python3-hatchling-native"

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-ctypes \
    python3-json \
    python3-logging \
    python3-threading \
"

RDEPENDS:${PN}-ptest += "\
    python3-pytest \
    python3-pytest-mock \
    python3-pytest-asyncio \
    python3-pytest-cov \
    python3-pytest-benchmark \
    python3-aiohttp \
    python3-numpy \
    python3-requests \
    python3-pandas \
    python3-paramiko \
"

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/tests ${D}${PTEST_PATH}/implementations
    cp -rf ${S}/fsspec/tests/* ${D}${PTEST_PATH}/tests/
    cp -rf ${S}/fsspec/implementations/tests ${D}${PTEST_PATH}/implementations/
    touch ${D}${PTEST_PATH}/implementations/__init__.py
    # Top-level conftest provides shared fixtures such as "m" and the
    # autouse instance cache cleaner
    install -m 0644 ${S}/fsspec/conftest.py ${D}${PTEST_PATH}/
    # Tests import helpers via fsspec.tests / fsspec.implementations.tests,
    # which aren't in the installed package
    grep -rlE 'fsspec\.(implementations\.)?tests' ${D}${PTEST_PATH} \
        | xargs -r sed -i -E 's/fsspec\.(implementations\.)?tests\b/\1tests/g'
}

BBCLASSEXTEND = "native nativesdk"
