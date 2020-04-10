SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://parse.py;beginline=1325;endline=1345;md5=fa03bae3f51a2db25e239e869c647437"

SRC_URI[md5sum] = "07cbb5ba1025cbfe92fc3376c372dda5"
SRC_URI[sha256sum] = "95a4f4469e37c57b5e924629ac99926f28bee7da59515dc5b8078c4c3e779249"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    "
