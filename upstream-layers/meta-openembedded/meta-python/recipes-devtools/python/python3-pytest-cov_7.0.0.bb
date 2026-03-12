SUMMARY = "Pytest plugin for measuring coverage."
HOMEPAGE = "https://github.com/pytest-dev/pytest-cov"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=cbc4e25353c748c817db2daffe605e43 \
"

SRC_URI[sha256sum] = "33c97eda2e049a0c5298e91f519302a1334c26ac65c1a483d6206fd458361af1"

inherit pypi python_setuptools_build_meta python_hatchling

DEPENDS += "python3-setuptools-scm-native python3-hatch-fancy-pypi-readme-native"
RDEPENDS:${PN} += "python3-coverage python3-pytest python3-pluggy"

PYPI_PACKAGE = "pytest_cov"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND = "native nativesdk"
