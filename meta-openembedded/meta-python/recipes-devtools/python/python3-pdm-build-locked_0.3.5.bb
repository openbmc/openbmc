SUMMARY = "pdm plugin to publish locked dependencies as optional-dependencies"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19382cdf9c143df4f00b9caa0b60c75a"

SRC_URI[sha256sum] = "ab2f381e00d79841d46be2e6909c265038b9fa951de2bf551ca6adb7f6844201"

inherit pypi python_setuptools_build_meta

DEPENDS += " python3-pdm-backend-native"

PYPI_PACKAGE = "pdm_build_locked"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

BBCLASSEXTEND += "native nativesdk"
