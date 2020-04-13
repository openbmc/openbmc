SUMMARY = "A Pure Python Expect like Module for Python"
HOMEPAGE = "http://pexpect.readthedocs.org/"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c7a725251880af8c6a148181665385b"

SRC_URI += "file://0001-FSM.py-change-shebang-from-python-to-python3.patch"

SRC_URI[md5sum] = "153eb25184249d6a85fde9acf4804085"
SRC_URI[sha256sum] = "fc65a43959d153d0114afe13997d439c22823a27cefceb5ff35c2178c6784c0c"

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
