SUMMARY = "Data validation and settings management using Python type hinting"
HOMEPAGE = "https://github.com/samuelcolvin/pydantic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c02ea30650b91528657db64baea1757"

inherit pypi setuptools3

SRC_URI[sha256sum] = "cfc83c0678b6ba51b0532bea66860617c4cd4251ecf76e9846fa5a9f3454e97e"

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
