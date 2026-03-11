SUMMARY = "Fork of the standard library cgi and cgitb modules, being deprecated in PEP-594"
HOMEPAGE = "https://github.com/jackrosenthal/legacy-cgi"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4b8801e752a2c70ac41a5f9aa243f766"

PYPI_PACKAGE = "legacy_cgi"

inherit python_poetry_core pypi

SRC_URI += "\
    file://0001-cgi.py-fixup-interpreter-according-to-OE.patch \
"

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

#RDEPENDS:${PN} = "python3-core"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "9952471ceb304043b104c22d00b4f333cac27a6abe446d8a528fc437cf13c85f"
