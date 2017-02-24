SUMMARY = "Simple powerful teting with python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8a5609608d9a5a180705b0fa298ae01"

SRC_URI[md5sum] = "6e736a6d7bc854b25d44a60bcba5f2f9"
SRC_URI[sha256sum] = "e82bc0596ee96b2287c08705cfcb6898db1fe4b5c87db3b6823f1fdd77fb3ff1"

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
