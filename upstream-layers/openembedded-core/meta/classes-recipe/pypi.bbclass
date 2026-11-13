#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

def pypi_default_package(d):
    """
    Return a reasonable guess for the PyPI package name by
    stripping any python- prefix from PN and normalising it.
    """
    bpn = d.getVar('BPN')
    if bpn.startswith('python-'):
        return pypi_package_normalise(bpn[7:])
    elif bpn.startswith('python3-'):
        return pypi_package_normalise(bpn[8:])
    return pypi_package_normalise(bpn)

# The PyPI package name, normalised as per
# https://packaging.python.org/en/latest/specifications/name-normalization/.
# Defaults to BPN without a python3- prefix.
PYPI_PACKAGE ?= "${@pypi_default_package(d)}"

# The name of the sdist. This defaults to the normalised name with underscores as per
# https://packaging.python.org/en/latest/specifications/source-distribution-format/,
# but can be overridden if needed.
PYPI_PACKAGE_SDIST ?= "${@d.getVar("PYPI_PACKAGE").replace("-", "_")}"

# The file extension of the source archive
PYPI_PACKAGE_EXT ?= "tar.gz"

# An optional prefix for the download file in the case of name collisions
PYPI_ARCHIVE_NAME_PREFIX ?= ""

def pypi_src_uri(d):
    """
    Construct a source URL as per https://docs.pypi.org/api/#predictable-urls.
    """
    package = d.getVar('PYPI_PACKAGE_SDIST')
    archive_name = d.expand('${PYPI_PACKAGE_SDIST}-${PV}.${PYPI_PACKAGE_EXT}')
    url = 'https://files.pythonhosted.org/packages/source/%s/%s/%s' % (package[0], package, archive_name)

    download_prefix = d.getVar("PYPI_ARCHIVE_NAME_PREFIX")
    if download_prefix:
        url += ";downloadfilename=" + download_prefix + archive_name

    return url

PYPI_SRC_URI ?= "${@pypi_src_uri(d)}"

HOMEPAGE ?= "https://pypi.python.org/pypi/${PYPI_PACKAGE}/"
SECTION = "devel/python"
SRC_URI:prepend = "${PYPI_SRC_URI} "
S = "${UNPACKDIR}/${PYPI_PACKAGE_SDIST}-${PV}"

# More information on the PyPI API specification is available here:
# https://packaging.python.org/en/latest/specifications/simple-repository-api/
#
# Use a case-insensitive regex wildcard instead of hyphen as the filenames may
# or may not have been normalised.
UPSTREAM_CHECK_URI ?= "https://pypi.org/simple/${PYPI_PACKAGE}/"
UPSTREAM_CHECK_REGEX ?= "(?i)${@d.getVar("PYPI_PACKAGE").replace("-", "[_-]")}-(?P<pver>(\d+(\.[\d\-]+)*(\.post\d+)?))\.(tar\.gz|tgz|zip|tar\.bz2)"

CVE_PRODUCT ?= "python:${PYPI_PACKAGE}"

# Generate ecosystem-specific Package URL for SPDX
SPDX_PACKAGE_URLS =+ "pkg:pypi/${PYPI_PACKAGE}@${PV} "

def pypi_package_normalise(s):
    """
    Normalise the passed package name as per
    https://packaging.python.org/en/latest/specifications/name-normalization/
    """
    import re
    return re.sub(r"[-_.]+", "-", s).lower()
