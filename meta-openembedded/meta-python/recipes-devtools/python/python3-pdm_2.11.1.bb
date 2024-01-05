SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "b10bc4e5394856f1639ddc9bc754d9c26323ec5b828a135c6ed35f935b054b83"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    ${PYTHON_PN}-pdm-backend-native \
"

BBCLASSEXTEND = "native nativesdk"
