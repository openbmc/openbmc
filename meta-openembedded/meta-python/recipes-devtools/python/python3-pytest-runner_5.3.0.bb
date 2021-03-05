SUMMARY = "Invoke py.test as distutils command with dependency resolution"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "ca3f58ff4957e8be6c54c55d575b235725cbbcf4dc0d5091c29c6444cfc8a5fe"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
