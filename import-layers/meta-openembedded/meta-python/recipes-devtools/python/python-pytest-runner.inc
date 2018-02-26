SUMMARY = "Invoke py.test as distutils command with dependency resolution"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e38b971c2b4c33b978d1b9c9ece9ae63"

SRC_URI[md5sum] = "a8dd57c80cde37a587dd8c37740c1ab1"
SRC_URI[sha256sum] = "5c9093e7a18780409d9a389f20366f2fd39acfb1db61545fdb2e90cdbd0bcae4"

inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} = "${PYTHON_PN}-py ${PYTHON_PN}-setuptools ${PYTHON_PN}-argparse ${PYTHON_PN}-debugger ${PYTHON_PN}-json \
                  ${PYTHON_PN}-io"

BBCLASSEXTEND = "native nativesdk"
