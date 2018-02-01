SUMMARY = "Parse strings using a specification based on the Python format() syntax"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://parse.py;md5=083d8ef8f98a3035dbf890a808498fde;startline=1189;endline=1208"

SRC_URI[md5sum] = "6ea7e32cb35810113137f6073fb30639"
SRC_URI[sha256sum] = "8b4f28bbe7c0f24981669ea92b2ba704ee63b5346027e82be30118bb5788ff10"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    "
