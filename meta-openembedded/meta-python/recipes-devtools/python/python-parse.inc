SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://parse.py;beginline=1317;endline=1337;md5=fa03bae3f51a2db25e239e869c647437"

SRC_URI[md5sum] = "8fc634769f1d841f14a52dd731ca447a"
SRC_URI[sha256sum] = "a5fca7000c6588d77bc65c28f3f21bfce03b5e44daa8f9f07c17fe364990d717"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    "
