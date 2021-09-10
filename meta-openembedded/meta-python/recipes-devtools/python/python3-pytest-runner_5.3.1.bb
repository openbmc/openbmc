SUMMARY = "Invoke py.test as distutils command with dependency resolution"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "0fce5b8dc68760f353979d99fdd6b3ad46330b6b1837e2077a89ebcf204aac91"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
