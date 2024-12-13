SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "7fe235a9cb27b7ec17b762dc85bc9ae71e0776e4b7a8b6f64203f24bb915aa2c"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-pdm-backend-native \
    python3-pdm-build-locked-native \
"

BBCLASSEXTEND = "native nativesdk"
