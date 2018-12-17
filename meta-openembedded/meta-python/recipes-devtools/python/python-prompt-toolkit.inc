SUMMARY = "Library for building powerful interactive command lines in Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2cde7da89f0c1f3e49bf968d00d554f"

SRC_URI[md5sum] = "8db8b8e40e91bc6dce133d8d5727a717"
SRC_URI[sha256sum] = "ff58ce8bb82c11c43416dd3eec7701dcbe8c576e2d7649f1d2b9d21a2fd93808"

PYPI_PACKAGE = "prompt_toolkit"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-wcwidth \
"

BBCLASSEXTEND = "native nativesdk"
