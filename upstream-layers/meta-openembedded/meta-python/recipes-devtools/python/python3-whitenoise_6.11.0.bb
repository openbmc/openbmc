SUMMARY = "Radically simplified static file serving for WSGI applications"
HOMEPAGE = "https://whitenoise.evans.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aba4901cc64e401cea5a267eac2a2e1e"

PYPI_PACKAGE = "whitenoise"

SRC_URI[sha256sum] = "0f5bfce6061ae6611cd9396a8231e088722e4fc67bc13a111be74c738d99375f"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:append = " \
    python3-brotli \
    python3-coverage \
    python3-django \
    python3-pytest \
    python3-requests \
"
