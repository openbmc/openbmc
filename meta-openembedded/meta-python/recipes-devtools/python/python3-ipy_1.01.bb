SUMMARY = "IPy - class and tools for handling of IPv4 and IPv6 addresses and networks"
DESCRIPTION = "IPy is a Python module for handling IPv4 and IPv6 Addresses and Networks \
in a fashion similar to perl's Net::IP and friends. The IP class allows \
a comfortable parsing and handling for most notations in use for IPv4 \
and IPv6 Addresses and Networks."
SECTION = "devel/python"
HOMEPAGE = "https://github.com/autocracy/python-ipy"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=848d24919845901b4f48bae5f13252e6"

SRC_URI[sha256sum] = "edeca741dea2d54aca568fa23740288c3fe86c0f3ea700344571e9ef14a7cc1a"

inherit pypi setuptools3_legacy ptest-python-pytest

PTEST_PYTEST_DIR = "test"

PYPI_PACKAGE = "IPy"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND = "native"
