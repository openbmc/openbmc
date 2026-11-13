SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "6bb5d893d301edb2b83d46042039634bf36c96c57cc7edad22a59521a41df9f8"

inherit pypi python_pdm

DEPENDS += " \
    python3-pdm-build-locked-native \
"

BBCLASSEXTEND = "native nativesdk"
