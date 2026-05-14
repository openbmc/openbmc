SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "d8ef85d2c4306ee67195412d701fae9983e84ec6574598e26798ae26b7b3c7e0"

inherit pypi python_pep517

PYPI_PACKAGE = "pdm_backend"

BBCLASSEXTEND = "native nativesdk"
