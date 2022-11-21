SUMMARY = "Hatch plugin for fancy PyPI readmes "
HOMEPAGE = "https://pypi.org/project/hatch-fancy-pypi-readme/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ba5633c60bd3e243091013aa83b4d807"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_fancy_pypi_readme"

SRC_URI[sha256sum] = "da91282ca09601c18aded8e378daf8b578c70214866f0971156ee9bb9ce6c26a"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/hatch-fancy-pypi-readme/(?P<pver>(\d+[\.\-_]*)+)/"
