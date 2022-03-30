DESCRIPTION = "Alabaster is a visually (c)lean, responsive, configurable theme for the Sphinx documentation system. It is Python 2+3 compatible."
HOMEPAGE = "https://alabaster.readthedocs.io/en/latest/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=da053683d66d543813a727e8a30c96ca"

SRC_URI[sha256sum] = "a661d72d58e6ea8a57f7a86e37d86716863ee5e92788398526d58b26a4e4dc02"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
