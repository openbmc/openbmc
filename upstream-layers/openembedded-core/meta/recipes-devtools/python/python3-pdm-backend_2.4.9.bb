SUMMARY = "The build backend used by PDM that supports latest packaging standards"
HOMEPAGE = "https://github.com/pdm-project/pdm-backend"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4a564297b3c5b629a528b92fd8ff61ea"

SRC_URI[sha256sum] = "c41a852ccbf4b567b033db9b2ae78660d7a4ea49307719959ab88a01f0aabb2e"

inherit pypi python_pep517

PYPI_PACKAGE = "pdm_backend"

BBCLASSEXTEND = "native nativesdk"
