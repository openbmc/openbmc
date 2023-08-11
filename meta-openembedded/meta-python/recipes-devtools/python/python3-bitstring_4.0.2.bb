SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=661f450e2c0aef39b4b15597333444a7"

SRC_URI[sha256sum] = "a391db8828ac4485dd5ce72c80b27ebac3e7b989631359959e310cd9729723b2"

PYPI_PACKAGE = "bitstring"

inherit pypi python_poetry_core

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-mmap \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
