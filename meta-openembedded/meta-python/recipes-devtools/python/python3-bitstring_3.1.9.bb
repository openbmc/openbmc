SUMMARY = "Simple construction, analysis and modification of binary data."
HOMEPAGE = "https://github.com/scott-griffiths/bitstring"
AUTHOR = "Scott Griffiths <dr.scottgriffiths@gmail.com>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f7f2fd3f1cd52b5ccd39d76fb3568d3f"

SRC_URI[sha256sum] = "a5848a3f63111785224dca8bb4c0a75b62ecdef56a042c8d6be74b16f7e860e7"

PYPI_PACKAGE = "bitstring"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-mmap \
"

BBCLASSEXTEND = "native nativesdk"
