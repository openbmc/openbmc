SUMMARY = "Python support for YAML"
HOMEPAGE = "http://www.pyyaml.org"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5591701d32590f9fa94f3bfee820b634"
DEPENDS = "libyaml ${PYTHON_PN}-cython-native ${PYTHON_PN}"

PYPI_PACKAGE = "PyYAML"
inherit pypi

SRC_URI[md5sum] = "b78b96636d68ac581c0e2f38158c224f"
SRC_URI[sha256sum] = "3ef3092145e9b70e3ddd2c7ad59bdd0252a94dfe3949721633e41344de00a6bf"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
"

BBCLASSEXTEND = "native nativesdk"
