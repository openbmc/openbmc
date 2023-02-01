DESCRIPTION = "Alabaster is a visually (c)lean, responsive, configurable theme for the Sphinx documentation system. It is Python 2+3 compatible."
HOMEPAGE = "https://alabaster.readthedocs.io/en/latest/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=21860fdb805bf4e0bfaf94b566b747fa"

SRC_URI[sha256sum] = "a27a4a084d5e690e16e01e03ad2b2e552c61a65469419b907243193de1a84ae2"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
