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

# The PyPi package name (defaults to PN without the python3- prefix)
PYPI_PACKAGE ?= "${@pypi_package(d)}"
# The file extension of the source archive
PYPI_PACKAGE_EXT ?= "tar.gz"
# An optional prefix for the download file in the case of name collisions
PYPI_ARCHIVE_NAME_PREFIX ?= ""

def pypi_src_uri(d):
    """
    Construct a source URL as per https://warehouse.pypa.io/api-reference/integration-guide.html#predictable-urls.
    """
    package = d.getVar('PYPI_PACKAGE')
    archive_name = d.expand('${PYPI_PACKAGE}-${PV}.${PYPI_PACKAGE_EXT}')
    archive_downloadname = d.getVar('PYPI_ARCHIVE_NAME_PREFIX') + archive_name
    return 'https://files.pythonhosted.org/packages/source/%s/%s/%s;downloadfilename=%s' % (package[0], package, archive_name, archive_downloadname)

def pypi_normalize(d):
    """"
        Normalize the package names to match PEP625 (https://peps.python.org/pep-0625/).
        For non-compliant packages, maintainers can set UPSTREAM_CHECK_PYPI_PACKAGE to override the normalization
    """
    import re
    return re.sub(r"[-_.]+", "-", d.getVar('PYPI_PACKAGE')).lower()

PYPI_SRC_URI ?= "${@pypi_src_uri(d)}"

HOMEPAGE ?= "https://pypi.python.org/pypi/${PYPI_PACKAGE}/"
SECTION = "devel/python"
SRC_URI:prepend = "${PYPI_SRC_URI} "
S = "${WORKDIR}/${PYPI_PACKAGE}-${PV}"

# Replace any '_' characters in the pypi URI with '-'s to follow the PyPi website naming conventions
UPSTREAM_CHECK_PYPI_PACKAGE ?= "${@pypi_normalize(d)}"

# Use the simple repository API rather than the potentially unstable project URL
# More information on the pypi API specification is avaialble here:
# https://packaging.python.org/en/latest/specifications/simple-repository-api/
#
# NOTE: All URLs for the simple API MUST request canonical normalized URLs per the spec
UPSTREAM_CHECK_URI ?= "https://pypi.org/simple/${@pypi_normalize(d)}"
UPSTREAM_CHECK_REGEX ?= "${UPSTREAM_CHECK_PYPI_PACKAGE}-(?P<pver>(\d+[\.\-_]*)+).(tar\.gz|tgz|zip|tar\.bz2)"

CVE_PRODUCT ?= "python:${PYPI_PACKAGE}"
