SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=661f450e2c0aef39b4b15597333444a7"

SRC_URI[sha256sum] = "c22283d60fd3e1a8f386ccd4f1915d7fe13481d6349db39711421e24d4a9cccf"

PYPI_PACKAGE = "bitstring"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-mmap \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
