SUMMARY = "A sphinx extension for designing beautiful, view size responsive web components"
HOMEPAGE = "https://sphinx-design.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=484a29024499c025d11c52f22a6f0bb8"

SRC_URI[sha256sum] = "d2a3f5b19c24b916adb52f97c5f00efab4009ca337812001109084a740ec9b7a"

inherit pypi python_flit_core

PYPI_PACKAGE = "sphinx_design"

BBCLASSEXTEND = "native nativesdk"
