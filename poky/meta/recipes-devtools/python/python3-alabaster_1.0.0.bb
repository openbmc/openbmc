SUMMARY = "Alabaster is a visually (c)lean, responsive, configurable theme for the Sphinx documentation system."
HOMEPAGE = "https://alabaster.readthedocs.io/en/latest/"
BUGTRACKER = "https://github.com/sphinx-doc/alabaster/issues"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=21860fdb805bf4e0bfaf94b566b747fa"

SRC_URI[sha256sum] = "c00dca57bca26fa62a6d7d0a9fcce65f3e026e9bfe33e9c538fd3fbb2144fd9e"

inherit python_flit_core pypi

BBCLASSEXTEND = "native nativesdk"
