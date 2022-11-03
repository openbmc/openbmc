SUMMARY = "Hatch plugin for fancy PyPI readmes "
HOMEPAGE = "https://pypi.org/project/hatch-fancy-pypi-readme/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ba5633c60bd3e243091013aa83b4d807"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_fancy_pypi_readme"

SRC_URI[sha256sum] = "dedf2ba0b81a2975abb1deee9310b2eb85d22380fda0d52869e760b5435aa596"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/hatch-fancy-pypi-readme/(?P<pver>(\d+[\.\-_]*)+)/"
