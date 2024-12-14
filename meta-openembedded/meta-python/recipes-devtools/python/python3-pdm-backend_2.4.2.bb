SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "1f833e527ae172f34b4b84e2fcf1f65859a2a5ca746e496d8313b3ea6539969f"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE= "pdm_backend"

BBCLASSEXTEND = "native nativesdk"
