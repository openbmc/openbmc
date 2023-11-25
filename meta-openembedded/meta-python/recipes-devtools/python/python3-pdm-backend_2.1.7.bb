SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "d3b50ab6374557c1edc348135e0da5decef228ddf8c973a58e40f437cf3595ba"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE= "pdm_backend"

BBCLASSEXTEND = "native nativesdk"
