SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "e39ed2da206d90d4a6e9eb62f6dce54ed4fa65ddf172a7d5700960d0f8a09e09"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE= "pdm_backend"

BBCLASSEXTEND = "native nativesdk"
