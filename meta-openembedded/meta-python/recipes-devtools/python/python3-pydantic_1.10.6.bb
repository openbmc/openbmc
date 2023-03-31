SUMMARY = "Data validation and settings management using Python type hinting"
HOMEPAGE = "https://github.com/samuelcolvin/pydantic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c02ea30650b91528657db64baea1757"

inherit pypi setuptools3

SRC_URI[sha256sum] = "cf95adb0d1671fc38d8c43dd921ad5814a735e7d9b4d9e437c088002863854fd"

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
