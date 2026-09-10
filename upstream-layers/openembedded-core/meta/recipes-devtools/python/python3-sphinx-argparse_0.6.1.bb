SUMMARY = "A sphinx extension that automatically documents argparse commands and options"
HOMEPAGE = "https://sphinx-argparse.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5c1cd8f13774629fee215681e66a1056"

SRC_URI[sha256sum] = "f8be167aedf5670bd7c812ef515968ff4717c63cfc9cb4df5f34b54fd5649c37"

PYPI_PACKAGE = "sphinx_argparse"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
