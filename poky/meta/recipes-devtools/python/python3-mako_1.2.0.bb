SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b32291f107a8f1ea94c4a41e00a6a18d"

PYPI_PACKAGE = "Mako"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "9a7c7e922b87db3686210cf49d5d767033a41d4010b284e747682c92bddd8b39"

RDEPENDS:${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-markupsafe \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-pygments \
                  ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
