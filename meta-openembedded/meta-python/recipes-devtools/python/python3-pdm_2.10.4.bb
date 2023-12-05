SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "6dfd9d4cb59043edecb2d0b47d208e55d89d333ba7197deb05cca2dfbc7a4bfb"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    ${PYTHON_PN}-pdm-backend-native \
"

BBCLASSEXTEND = "native nativesdk"
