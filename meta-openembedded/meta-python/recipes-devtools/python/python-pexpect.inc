SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c7a725251880af8c6a148181665385b"

SRC_URI[md5sum] = "d4f3372965a996238d57d19b95d2e03a"
SRC_URI[sha256sum] = "2a8e88259839571d1251d278476f3eec5db26deb73a70be5ed5dc5435e418aba"

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
