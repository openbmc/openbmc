SUMMARY = "Simple powerful teting with python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8a5609608d9a5a180705b0fa298ae01"

SRC_URI[md5sum] = "6639592fa430567d024189f097fcdbd7"
SRC_URI[sha256sum] = "643434a9f1a188271da35e20064cb8b6c5440976c5bb541dc7b5b0e3cf75d940"

RDEPENDS_${PN}_class-target += " \
    ${PYTHON_PN}-argparse \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-py \
    ${PYTHON_PN}-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
