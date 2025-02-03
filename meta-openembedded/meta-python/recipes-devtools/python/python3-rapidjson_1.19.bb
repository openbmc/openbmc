SUMMARY = "Python wrapper around rapidjson"
HOMEPAGE = "https://github.com/python-rapidjson/python-rapidjson"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6fe0b2465366662d7cfa6793ccbab563"

SRC_URI[sha256sum] = "81e7260f6297cad564389b700783c0a33de71310b9eb01fd013faec5e7ed4eff"

# Inheriting ptest provides functionality for packaging and installing runtime tests for this recipe
inherit setuptools3 pypi ptest-python-pytest

PYPI_PACKAGE = "python-rapidjson"

SETUPTOOLS_BUILD_ARGS += " --rj-include-dir=${RECIPE_SYSROOT}${includedir}"

DEPENDS += " \
    rapidjson \
"

# Adding required python package for the ptest (pytest and pytest->automake report translation)
RDEPENDS:${PN}-ptest += " \
    python3-pytz \
"

RDEPENDS:${PN} += " \
    python3-core \
"

