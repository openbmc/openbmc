SUMMARY = "pdm plugin to publish locked dependencies as optional-dependencies"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19382cdf9c143df4f00b9caa0b60c75a"

SRC_URI[sha256sum] = "46dd94f6134e351a8885754ffef95a395e774f1f338348b763f9a208fd19efd7"

inherit pypi python_setuptools_build_meta

DEPENDS += " python3-pdm-backend-native"

PYPI_PACKAGE = "pdm_build_locked"

BBCLASSEXTEND += "native nativesdk"
