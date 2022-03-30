SUMMARY = "Python support for YAML"
DEPENDS += "libyaml ${PYTHON_PN}-cython-native"
HOMEPAGE = "https://pyyaml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

PYPI_PACKAGE = "PyYAML"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "68fb519c14306fec9720a2a5b45bc9f0c8d1b9c72adf45c37baedfcd949c35a2"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

BBCLASSEXTEND = "native nativesdk"
