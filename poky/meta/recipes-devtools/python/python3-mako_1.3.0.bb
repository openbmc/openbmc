SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcc01df649aee6c59dcb254c894ea0d4"

PYPI_PACKAGE = "Mako"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "e3a9d388fd00e87043edbe8792f45880ac0114e9c4adc69f6e9bfb2c55e3b11b"

RDEPENDS:${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-markupsafe \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-pygments \
                  ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
