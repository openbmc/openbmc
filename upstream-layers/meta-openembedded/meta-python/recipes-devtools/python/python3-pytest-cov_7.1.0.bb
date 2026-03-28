SUMMARY = "Pytest plugin for measuring coverage."
HOMEPAGE = "https://github.com/pytest-dev/pytest-cov"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=cbc4e25353c748c817db2daffe605e43 \
"

SRC_URI[sha256sum] = "30674f2b5f6351aa09702a9c8c364f6a01c27aae0c1366ae8016160d1efc56b2"

inherit pypi python_setuptools_build_meta python_hatchling

DEPENDS += "python3-setuptools-scm-native python3-hatch-fancy-pypi-readme-native"
RDEPENDS:${PN} += "python3-coverage python3-pytest python3-pluggy"

PYPI_PACKAGE = "pytest_cov"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND = "native nativesdk"
