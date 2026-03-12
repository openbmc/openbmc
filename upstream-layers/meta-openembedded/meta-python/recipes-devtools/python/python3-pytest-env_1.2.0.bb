SUMMARY = "pytest plugin that allows you to add environment variables."
HOMEPAGE = "https://github.com/pytest-dev/pytest-env"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b70ef84b3d8d608b13b0287ed49df651"
RECIPE_MAINTAINER = "Tom Geelen <t.f.g.geelen@gmail.com>"

DEPENDS = "python3-hatch-vcs-native python3-hatchling-native"
SRC_URI[sha256sum] = "475e2ebe8626cee01f491f304a74b12137742397d6c784ea4bc258f069232b80"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "pytest_env"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "python3-pytest python3-tomli"

RDEPENDS:${PN}-ptest += " \
    python3-covdefaults \
    python3-coverage \
    python3-pytest-mock \
"
