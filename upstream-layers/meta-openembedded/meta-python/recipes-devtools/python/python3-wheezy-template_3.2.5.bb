SUMMARY = "a lightweight template library"
HOMEPAGE = "https://github.com/akornatskyy/wheezy.template"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa10554c46b94944529c6a886cf85631"

PYPI_PACKAGE = "wheezy_template"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "c7c0bf85af0f70ca2ef4b6ea9a74ef372f73392aa17bea0d885dcba7356d0867"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native"
