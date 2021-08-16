SUMMARY = "IPython: Productive Interactive Computing"
HOMEPAGE = "https://ipython.org"
AUTHOR = "The IPython Development Team <ipython-dev@python.org>"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=59b20262b8663cdd094005bddf47af5f"

PYPI_PACKAGE = "ipython"

SRC_URI[sha256sum] = "0cff04bb042800129348701f7bd68a430a844e8fb193979c08f6c99f28bb735e"

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
"

inherit setuptools3 pypi
