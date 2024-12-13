DESCRIPTION="YAML Path and Command-Line Tools"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5abda174c5040dd12ed2b225e3a096f0"

SRC_URI[sha256sum] = "81d5b8baba60c255b519ccd31a691f9bc064223ff196709d41119bde81bba49e"

PYPI_PACKAGE = "yamlpath"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"
