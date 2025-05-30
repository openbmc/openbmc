SUMMARY = "Hatch plugin for fancy PyPI readmes "
HOMEPAGE = "https://pypi.org/project/hatch-fancy-pypi-readme/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ba5633c60bd3e243091013aa83b4d807"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_fancy_pypi_readme"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "9c58ed3dff90d51f43414ce37009ad1d5b0f08ffc9fc216998a06380f01c0045"

BBCLASSEXTEND = "native nativesdk"
