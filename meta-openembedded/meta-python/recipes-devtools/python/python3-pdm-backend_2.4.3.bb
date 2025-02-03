SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "dbd9047a7ac10d11a5227e97163b617ad5d665050476ff63867d971758200728"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "pdm_backend"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND = "native nativesdk"
