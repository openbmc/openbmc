SUMMARY = "pytest-sugar is a plugin for pytest that changes the default look and feel of pytest (e.g. progressbar, show tests that fail instantly)."
HOMEPAGE = "https://github.com/Teemu/pytest-sugar"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e485b36215d1a038821b3e51bd189197"

SRC_URI += "file://run-ptest"
SRC_URI[sha256sum] = "73b8b65163ebf10f9f671efab9eed3d56f20d2ca68bda83fa64740a92c08f65d"

inherit pypi python_poetry_core ptest-python-pytest

PACKAGECONFIG ?= ""
PACKAGECONFIG[dev] = ",,,python3-black python3-flake8 python3-pre-commit"

do_install_ptest:append() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/test_sugar.py ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "python3-packaging python3-pytest python3-termcolor \
                   python3-core python3-packaging python3-pytest \
				   python3-pytest-xdist"

RDEPENDS:${PN}-ptest +=  "python3-tox"
