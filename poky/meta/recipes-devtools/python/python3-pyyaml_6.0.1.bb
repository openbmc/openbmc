SUMMARY = "Python support for YAML"
DEPENDS += "libyaml ${PYTHON_PN}-cython-native"
HOMEPAGE = "https://pyyaml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

PYPI_PACKAGE = "PyYAML"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "bfdf460b1736c775f2ba9f6a92bca30bc2095067b8a9d77876d1fad6cc3b4a43"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-netclient \
"

BBCLASSEXTEND = "native nativesdk"
