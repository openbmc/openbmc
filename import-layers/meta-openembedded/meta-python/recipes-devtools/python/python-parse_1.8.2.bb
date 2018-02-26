SUMMARY = "Parse strings using a specification based on the Python format() syntax"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://parse.py;md5=a84b494563bba466a2f02b05f2e6ad4a;startline=1191;endline=1210"

SRC_URI[md5sum] = "42002338551bdfa0f01bbe4e679a17dd"
SRC_URI[sha256sum] = "8048dde3f5ca07ad7ac7350460952d83b63eaacecdac1b37f45fd74870d849d2"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    "
