SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=49;endline=49;md5=0f13c9cfc75288223cf7edd2f3b985a2"

SRC_URI[md5sum] = "576b78add6d652b48dcf9362cece88ef"
SRC_URI[sha256sum] = "b4e2fb9fc656adccc4d01dfd55ab5a9b05890e961950543f692e7885725c2d72"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-textutils \
    "
