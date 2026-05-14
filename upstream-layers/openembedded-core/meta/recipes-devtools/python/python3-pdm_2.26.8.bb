SUMMARY = "A modern Python package and dependency manager supporting the latest PEP standards"
HOMEPAGE = "https://pdm-project.org/latest/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2eb31a2cc1a758c34b499f287dd04ef2"

SRC_URI[sha256sum] = "f53a20b4b6beaa88da577a7413ebbb51bee534441339b006dba2260edbc81757"

inherit pypi python_pdm

DEPENDS += " \
    python3-pdm-build-locked-native \
"

BBCLASSEXTEND = "native nativesdk"
