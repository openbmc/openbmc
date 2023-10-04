#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

def pypi_package(d):
    bpn = d.getVar('BPN')
    if bpn.startswith('python-'):
        return bpn[7:]
    elif bpn.startswith('python3-'):
        return bpn[8:]
    return bpn

PYPI_PACKAGE ?= "${@pypi_package(d)}"
PYPI_PACKAGE_EXT ?= "tar.gz"
PYPI_ARCHIVE_NAME ?= "${PYPI_PACKAGE}-${PV}.${PYPI_PACKAGE_EXT}"
PYPI_ARCHIVE_NAME_PREFIX ?= ""

def pypi_src_uri(d):
    package = d.getVar('PYPI_PACKAGE')
    archive_name = d.getVar('PYPI_ARCHIVE_NAME')
    archive_downloadname = d.getVar('PYPI_ARCHIVE_NAME_PREFIX') + archive_name
    return 'https://files.pythonhosted.org/packages/source/%s/%s/%s;downloadfilename=%s' % (package[0], package, archive_name, archive_downloadname)

PYPI_SRC_URI ?= "${@pypi_src_uri(d)}"

HOMEPAGE ?= "https://pypi.python.org/pypi/${PYPI_PACKAGE}/"
SECTION = "devel/python"
SRC_URI:prepend = "${PYPI_SRC_URI} "
S = "${WORKDIR}/${PYPI_PACKAGE}-${PV}"

# Replace any '_' characters in the pypi URI with '-'s to follow the PyPi website naming conventions
UPSTREAM_CHECK_PYPI_PACKAGE ?= "${@d.getVar('PYPI_PACKAGE').replace('_', '-')}"
UPSTREAM_CHECK_URI ?= "https://pypi.org/project/${UPSTREAM_CHECK_PYPI_PACKAGE}/"
UPSTREAM_CHECK_REGEX ?= "/${UPSTREAM_CHECK_PYPI_PACKAGE}/(?P<pver>(\d+[\.\-_]*)+)/"

CVE_PRODUCT ?= "python:${PYPI_PACKAGE}"
