SUMMARY = "Python support for YAML"
DEPENDS += "libyaml ${PYTHON_PN}-cython-native"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

PYPI_PACKAGE = "PyYAML"

inherit pypi setuptools3

SRC_URI[sha256sum] = "607774cbba28732bfa802b54baa7484215f530991055bb562efbed5b2f20a45e"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

BBCLASSEXTEND = "native nativesdk"
