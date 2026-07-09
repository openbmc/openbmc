SUMMARY = "the blessed package to manage your versions by vcs metadata"
HOMEPAGE = "https://pypi.org/project/vcs-versioning/"
DESCRIPTION = "Core VCS versioning functionality extracted as a standalone \
library that can be used independently of setuptools."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c9b06ad2ebd7e2e82d34b3caf353e7d5"

SRC_URI[sha256sum] = "4ac4ded78720cdb4d0291ae58ace87e1e9201912e1023f3029c6cce5c9152cfb"

SRC_URI += "\
    file://run-ptest \
"

PYPI_PACKAGE = "vcs_versioning"
PTEST_PYTEST_DIR = "testing_vcs"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "\
    python3-packaging-native \
    python3-typing-extensions-native \
"

RDEPENDS:${PN} = "\
    python3-packaging \
    python3-typing-extensions \
"

RDEPENDS:${PN}-ptest += "\
    git \
    python3-setuptools \
    python3-setuptools-scm \
"

do_install_ptest:append() {
    rm -rf ${D}${PTEST_PATH}/.pytest_cache
    find ${D}${PTEST_PATH} -name __pycache__ -type d -exec rm -rf {} +
    find ${D}${PTEST_PATH} -name '*.pyc' -delete
}

BBCLASSEXTEND = "native nativesdk"
