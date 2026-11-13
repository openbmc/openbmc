# SPDX-License-Identifier: MIT
# Copyright (C) 2023 iris-GmbH infrared & intelligent sensors
SUMMARY = "A single module, which implements a platform independent file lock in Python, which provides a simple way of inter-process communication"
HOMEPAGE = "https://py-filelock.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c6acbdf7bb74caa37512c3a5ca6857b"

CVE_PRODUCT = "tox-dev:filelock"

SRC_URI += "file://run-ptest"

SRC_URI[sha256sum] = "96530b7c538d4ea08493ab067247aab7948c54d8cd622cce711c97cccc5d9896"

BBCLASSEXTEND = "native nativesdk"

inherit pypi python_hatchling ptest-python-pytest

DEPENDS += "\
    python3-hatch-fancy-pypi-readme-native \
    python3-hatch-vcs-native \
"

RDEPENDS:${PN} += " \
    python3-core \
    python3-crypt \
    python3-logging \
    python3-asyncio \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest-asyncio \
    python3-pytest-mock \
    python3-pytest-timeout \
    python3-virtualenv \
    python3-discovery \
"

# tests/ imports itself as "tests.*" and pulls in "capabilities" from the
# sibling tasks/ dir; pyproject.toml's [tool.pytest] pythonpath=[".", "tasks"]
# is what upstream relies on to make both resolve.
do_install_ptest:append() {
    cp -rf ${S}/tasks ${D}${PTEST_PATH}/
    cp -f ${S}/pyproject.toml ${D}${PTEST_PATH}/
    # test_documented_cache_lock_name checks snippets in docs/how-to.rst
    install -Dm 0644 ${S}/docs/how-to.rst ${D}${PTEST_PATH}/docs/how-to.rst
}
