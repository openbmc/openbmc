SUMMARY = "pdm plugin to publish locked dependencies as optional-dependencies"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19382cdf9c143df4f00b9caa0b60c75a"

SRC_URI[sha256sum] = "b784135abf62b93ce0a11332ee24723a2699b7f266fddb7950a5b70c93df6214"

inherit pypi python_setuptools_build_meta

DEPENDS += " python3-pdm-backend-native"

PYPI_PACKAGE = "pdm_build_locked"

BBCLASSEXTEND += "native nativesdk"
