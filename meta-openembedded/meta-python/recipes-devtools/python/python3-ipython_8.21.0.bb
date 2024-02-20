SUMMARY = "IPython: Productive Interactive Computing"
HOMEPAGE = "https://ipython.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=59b20262b8663cdd094005bddf47af5f"

PYPI_PACKAGE = "ipython"

SRC_URI[sha256sum] = "48fbc236fbe0e138b88773fa0437751f14c3645fb483f1d4c5dee58b37e5ce73"

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
