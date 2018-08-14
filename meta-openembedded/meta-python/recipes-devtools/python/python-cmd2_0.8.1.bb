SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=51;endline=51;md5=0f13c9cfc75288223cf7edd2f3b985a2"

SRC_URI[md5sum] = "701b916ffac99137bea413205106f993"
SRC_URI[sha256sum] = "d09976f9ad2327883c2d07b5acb42e66ad52b17e352873c22041ed124bfe8aba"

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
