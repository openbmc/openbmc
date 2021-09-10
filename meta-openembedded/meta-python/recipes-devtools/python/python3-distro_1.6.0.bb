SUMMARY = "Distro is an OS platform information API"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

PYPI_PACKAGE = "distro"

SRC_URI[sha256sum] = "83f5e5a09f9c5f68f60173de572930effbcc0287bb84fdc4426cb4168c088424"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
