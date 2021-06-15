SUMMARY = "Easy manipulation of Python source via the AST."
HOMEPAGE = "https://github.com/berkerpeksag/astor"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=561205fdabc3ec52cae2d30815b8ade7"

SRC_URI = "git://github.com/berkerpeksag/astor.git \
           file://0001-rtrip.py-convert-to-python3.patch \
"
SRCREV ?= "c7553c79f9222e20783fe9bd8a553f932e918072"

inherit setuptools3

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
