SUMMARY = "Hatch plugin for fancy PyPI readmes "
HOMEPAGE = "https://pypi.org/project/hatch-fancy-pypi-readme/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ba5633c60bd3e243091013aa83b4d807"

inherit pypi python_hatchling

PYPI_PACKAGE = "hatch_fancy_pypi_readme"

SRC_URI[sha256sum] = "44dd239f1a779b9dcf8ebc9401a611fd7f7e3e14578dcf22c265dfaf7c1514b8"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/hatch-fancy-pypi-readme/(?P<pver>(\d+[\.\-_]*)+)/"
