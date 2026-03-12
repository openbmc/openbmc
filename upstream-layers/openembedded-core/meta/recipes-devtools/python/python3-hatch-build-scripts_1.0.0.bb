SUMMARY = "A plugin for Hatch that runs build scripts and saves their artifacts"
HOMEPAGE = "https://pypi.org/project/hatch_build_scripts/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9ad584cda56221c7eaf48c23a5874a2a"

PYPI_PACKAGE = "hatch_build_scripts"
SRC_URI[sha256sum] = "563735e2f265c9e1b92dece6f762309114505ffaf6e5d51d462eb6a3b4f14640"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"
