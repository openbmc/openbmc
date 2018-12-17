SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=60bc6842001870a418935bd84570b676"

SRC_URI[md5sum] = "bfe0900a2288283a40f0ee7533486a2f"
SRC_URI[sha256sum] = "e7b6b4b76272a051c943c3c709cd760142af16fbc9218e0ed3c22b28ea38d0a8"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-pyperclip \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-textutils \
    "

BBCLASSEXTEND = "native nativesdk"
