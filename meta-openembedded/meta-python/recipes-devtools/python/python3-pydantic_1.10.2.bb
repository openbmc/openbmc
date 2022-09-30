SUMMARY = "Data validation and settings management using Python type hinting"
HOMEPAGE = "https://github.com/samuelcolvin/pydantic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c02ea30650b91528657db64baea1757"

inherit pypi setuptools3

SRC_URI[sha256sum] = "91b8e218852ef6007c2b98cd861601c6a09f1aa32bbbb74fab5b1c33d4a1e410"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-typing-extensions \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-image \
    ${PYTHON_PN}-logging \
"
