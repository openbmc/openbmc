SUMMARY = "Run a subprocess in a pseudo terminal"
HOMEPAGE = "http://ptyprocess.readthedocs.io/en/latest/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cfdcd51fa7d5808da4e74346ee394490"

SRCNAME = "ptyprocess"

SRC_URI += "file://a44312974bd9084aa568d2e18ce5b2a7e0e45983.patch"
SRC_URI[sha256sum] = "5c5d0a3b48ceee0b48485e0c26037c0acd7d29765ca3fbb5cb3831d347423220"

inherit pypi python_flit_core ptest-python-pytest

S = "${WORKDIR}/${SRCNAME}-${PV}"

RDEPENDS:${PN} = "\
    python3-core \
    python3-fcntl \
    python3-terminal \
    python3-resource \
"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}-ptest += " \
    bash \
    python3-fcntl \
    python3-resource \
    python3-terminal \
"

