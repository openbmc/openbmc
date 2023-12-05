SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=661f450e2c0aef39b4b15597333444a7"

SRC_URI[sha256sum] = "1b47c84644a961ba8503db2bba8a5965ab53e81474becdf0a18383b5b5f3f795"

PYPI_PACKAGE = "bitstring"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-mmap \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
