SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "http://packages.python.org/cmd2/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://setup.py;beginline=17;endline=17;md5=25c0f7ede01b1eab41daf880e5791f05"

SRC_URI[md5sum] = "cf39b5a34955d263b42a7ffc9d3e536d"
SRC_URI[sha256sum] = "ef09745c91dbc13344db6d81f4dea4c844bf2fabf3baf91fab1bb54e4b3bb328"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-textutils \
    "
