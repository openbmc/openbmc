SUMMARY = "Templating library for Python"
HOMEPAGE = "http://www.makotemplates.org/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad08dd28df88e64b35bcac27c822ee34"

PYPI_PACKAGE = "Mako"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f054a5ff4743492f1aa9ecc47172cb33b42b9d993cffcc146c9de17e717b0307"

RDEPENDS:${PN} = "${PYTHON_PN}-html \
                  ${PYTHON_PN}-markupsafe \
                  ${PYTHON_PN}-netclient \
                  ${PYTHON_PN}-pygments \
                  ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
