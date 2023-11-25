SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "46dafc8a4fe268c46479876e52c6967f7a9aa385e1e574e64248670a37b358ff"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    ${PYTHON_PN}-pdm-backend-native \
"

BBCLASSEXTEND = "native nativesdk"
