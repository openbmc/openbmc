SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "7ae6c4b5518085a703e0824ab10cbc3b7246b599ef07b8c4dcb2af1e42859190"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-pdm-backend-native \
"

BBCLASSEXTEND = "native nativesdk"
