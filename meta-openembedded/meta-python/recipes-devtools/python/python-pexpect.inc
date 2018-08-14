SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c7a725251880af8c6a148181665385b"

SRC_URI[md5sum] = "913fc0a68a915cdf6ad4250493c94cf7"
SRC_URI[sha256sum] = "9f8eb3277716a01faafaba553d629d3d60a1a624c7cf45daa600d2148c30020c"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/pexpect"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-resource \
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-ptyprocess \
"

BBCLASSEXTEND = "native nativesdk"
