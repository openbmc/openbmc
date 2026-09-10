#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

def pypi_default_package(d):
    """
    Return a reasonable guess for the PyPI package name by
    stripping any python- prefix from PN.
    """
    bpn = d.getVar('BPN')
    if bpn.startswith('python-'):
        return bpn[7:]
    elif bpn.startswith('python3-'):
        return bpn[8:]
    return bpn

# The PyPi package name (defaults to PN without the python3- prefix)
PYPI_PACKAGE ?= "${@pypi_default_package(d)}"
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
    url = 'https://files.pythonhosted.org/packages/source/%s/%s/%s' % (package[0], package, archive_name)

    download_prefix = d.getVar("PYPI_ARCHIVE_NAME_PREFIX")
    if download_prefix:
        url += ";downloadfilename=" + download_prefix + archive_name

    return url

def pypi_normalize(d):
    """"
    Normalize the package names to match PEP625 (https://peps.python.org/pep-0625/).
    """
    import re
    return re.sub(r"[-_.]+", "-", d.getVar('PYPI_PACKAGE')).lower()

PYPI_SRC_URI ?= "${@pypi_src_uri(d)}"

HOMEPAGE ?= "https://pypi.python.org/pypi/${PYPI_PACKAGE}/"
SECTION = "devel/python"
SRC_URI:prepend = "${PYPI_SRC_URI} "
S = "${UNPACKDIR}/${PYPI_PACKAGE}-${PV}"

def pypi_normalize_regex(d):
    # Use a regex wildcard instead of hyphen as the filenames
    # may or may not have been normalised properly.
    return pypi_normalize(d).replace("-", "[_-]")

# Use the simple repository API rather than the potentially unstable project URL
# More information on the pypi API specification is avaialble here:
# https://packaging.python.org/en/latest/specifications/simple-repository-api/
#
# NOTE: All URLs for the simple API MUST request canonical normalized URLs per the spec
UPSTREAM_CHECK_URI ?= "https://pypi.org/simple/${@pypi_normalize(d)}/"
UPSTREAM_CHECK_REGEX ?= "(?i)${@pypi_normalize_regex(d)}-(?P<pver>(\d+(\.[\d\-]+)*(\.post\d+)?))\.(tar\.gz|tgz|zip|tar\.bz2)"

CVE_PRODUCT ?= "python:${PYPI_PACKAGE}"

# Generate ecosystem-specific Package URL for SPDX
SPDX_PACKAGE_URLS =+ "pkg:pypi/${@pypi_normalize(d)}@${PV} "
