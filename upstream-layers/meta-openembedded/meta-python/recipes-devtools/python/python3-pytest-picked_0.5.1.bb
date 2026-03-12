SUMMARY = "Run the tests related to the changed files"
HOMEPAGE = "https://github.com/anapaulagomes/pytest-picked"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d374a27c85c3fcc979009952ec16f1b"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

SRC_URI += "file://run-ptest \
           file://0001-adjust-failing-tests-to-capture-only-ptest-output.patch \
           "
SRC_URI[sha256sum] = "6634c4356a560a5dc3dba35471865e6eb06bbd356b56b69c540593e9d5620ded"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN} += "\
    git \
    python3-pytest (>=3.7.0) \
"

PYPI_PACKAGE = "pytest_picked"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
