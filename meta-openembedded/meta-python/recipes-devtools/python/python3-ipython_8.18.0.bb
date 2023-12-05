SUMMARY = "IPython: Productive Interactive Computing"
HOMEPAGE = "https://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=59b20262b8663cdd094005bddf47af5f"

PYPI_PACKAGE = "ipython"

SRC_URI[sha256sum] = "4feb61210160f75e229ce932dbf8b719bff37af123c0b985fd038b14233daa16"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-setuptools \
    ${PYTHON_PN}-jedi \
    ${PYTHON_PN}-decorator \
    ${PYTHON_PN}-pickleshare \
    ${PYTHON_PN}-traitlets \
    ${PYTHON_PN}-prompt-toolkit \
    ${PYTHON_PN}-pygments \
    ${PYTHON_PN}-backcall \
    ${PYTHON_PN}-pydoc \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-pexpect \
    ${PYTHON_PN}-unixadmin \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-sqlite3 \
    ${PYTHON_PN}-stack-data \
"

inherit setuptools3 pypi
