SUMMARY = "A sphinx extension that automatically documents argparse commands and options"
HOMEPAGE = "https://sphinx-argparse.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5c1cd8f13774629fee215681e66a1056"

SRC_URI[sha256sum] = "d072bb67dd52b294375f0eedc203cb8e50d0329910dbceb6764e9386bff94e9d"

PYPI_PACKAGE = "sphinx_argparse"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
