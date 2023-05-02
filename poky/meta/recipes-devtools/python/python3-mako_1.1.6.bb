SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=943eb67718222db21d44a4ef1836675f"

PYPI_PACKAGE = "Mako"

SRC_URI += "file://CVE-2022-40023.patch"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "4e9e345a41924a954251b95b4b28e14a301145b544901332e658907a7464b6b2"

RDEPENDS:${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
