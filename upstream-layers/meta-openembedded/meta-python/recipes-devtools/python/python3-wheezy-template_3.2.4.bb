SUMMARY = "a lightweight template library"
HOMEPAGE = "https://github.com/akornatskyy/wheezy.template"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa10554c46b94944529c6a886cf85631"

PYPI_PACKAGE = "wheezy_template"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "465b9ac52e1c38bc9fc30127ae90bd232ce8df07fc2ac53383cb784f238b144f"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native"
