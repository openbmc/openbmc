SUMMARY = "Invoke py.test as distutils command with dependency resolution"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a33f38bbf47d48c70fe0d40e5f77498e"

SRC_URI[md5sum] = "8f8363a52bbabc4cedd5e239beb2ba11"
SRC_URI[sha256sum] = "0f7c3a3cf5aead13f54baaa01ceb49e5ae92aba5d3ff1928e81e189c40bc6703"

inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
