SUMMARY = "Alabaster is a visually (c)lean, responsive, configurable theme for the Sphinx documentation system."
HOMEPAGE = "https://alabaster.readthedocs.io/en/latest/"
BUGTRACKER = "https://github.com/sphinx-doc/alabaster/issues"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=21860fdb805bf4e0bfaf94b566b747fa"

SRC_URI[sha256sum] = "75a8b99c28a5dad50dd7f8ccdd447a121ddb3892da9e53d1ca5cca3106d58d65"

inherit python_flit_core pypi

BBCLASSEXTEND = "native nativesdk"
