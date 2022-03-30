SUMMARY = "Invoke py.test as distutils command with dependency resolution"
HOMEPAGE = "https://pypi.org/project/pytest-runner/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "b4d85362ed29b4c348678de797df438f0f0509497ddb8c647096c02a6d87b685"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io ${PYTHON_PN}-distutils"

BBCLASSEXTEND = "native nativesdk"
