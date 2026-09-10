SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "28c520267a15d7a3921b934ec4bfc2d1dc18379fbb653bb20d8e840b2788be6b"

inherit pypi python_pdm

DEPENDS += " \
    python3-pdm-build-locked-native \
"

BBCLASSEXTEND = "native nativesdk"
