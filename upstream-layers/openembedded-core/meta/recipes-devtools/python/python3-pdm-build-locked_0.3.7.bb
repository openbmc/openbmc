SUMMARY = "pdm plugin to publish locked dependencies as optional-dependencies"
HOMEPAGE = "https://github.com/pdm-project/pdm-build-locked"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19382cdf9c143df4f00b9caa0b60c75a"

SRC_URI[sha256sum] = "53428268284125532413434ebfeb8e7a287525516cc5a0a055d63ba63b207165"

inherit pypi python_pdm

PYPI_PACKAGE = "pdm_build_locked"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND += "native nativesdk"
