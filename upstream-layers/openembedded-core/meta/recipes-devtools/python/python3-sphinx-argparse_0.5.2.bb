SUMMARY = "A sphinx extension that automatically documents argparse commands and options"
HOMEPAGE = "https://sphinx-argparse.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=5c1cd8f13774629fee215681e66a1056"

SRC_URI[sha256sum] = "e5352f8fa894b6fb6fda0498ba28a9f8d435971ef4bbc1a6c9c6414e7644f032"

PYPI_PACKAGE = "sphinx_argparse"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
