SUMMARY = "PyMongo support for Flask applications"
DESCRIPTION = "PyMongo support for Flask applications."
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://flask_pymongo/wrappers.py;beginline=1;endline=24;md5=424c4e1047d28e01b4e4634a069c019d"

SRC_URI[sha256sum] = "d225b51c21ceca2e670e6cca79b5c584ad17b96252b48e84e3b423ddb73304cc"

PYPI_PACKAGE = "flask_pymongo"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta python_hatchling

DEPENDS = " \
    python3-vcversioner \
    python3-vcversioner-native \
    python3-hatchling-native \
"

RDEPENDS:${PN} = "python3-pymongo python3-flask"
