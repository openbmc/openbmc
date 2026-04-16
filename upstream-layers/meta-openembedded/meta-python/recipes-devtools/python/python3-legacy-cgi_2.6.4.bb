SUMMARY = "Fork of the standard library cgi and cgitb modules, being deprecated in PEP-594"
HOMEPAGE = "https://github.com/jackrosenthal/legacy-cgi"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b8801e752a2c70ac41a5f9aa243f766"

PYPI_PACKAGE = "legacy_cgi"

inherit python_poetry_core pypi python_hatchling

SRC_URI += "\
    file://0001-cgi.py-fixup-interpreter-according-to-OE.patch \
"

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

# Add this function to solve package QA Issue
do_install:append() {
        rm ${D}${PYTHON_SITEPACKAGES_DIR}/.pc -rf
}

#RDEPENDS:${PN} = "python3-core"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "abb9dfc7835772f7c9317977c63253fd22a7484b5c9bbcdca60a29dcce97c577"
