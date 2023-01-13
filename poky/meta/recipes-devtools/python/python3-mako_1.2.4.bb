SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad08dd28df88e64b35bcac27c822ee34"

PYPI_PACKAGE = "Mako"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "d60a3903dc3bb01a18ad6a89cdbe2e4eadc69c0bc8ef1e3773ba53d44c3f7a34"

RDEPENDS:${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-markupsafe \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-pygments \
                  ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
