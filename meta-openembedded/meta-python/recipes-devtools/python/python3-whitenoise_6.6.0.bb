SUMMARY = "Radically simplified static file serving for WSGI applications"
AUTHOR = "David Evans <d@evans.io>"
HOMEPAGE = "https://whitenoise.evans.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aba4901cc64e401cea5a267eac2a2e1e"

PYPI_PACKAGE = "whitenoise"

SRC_URI[sha256sum] = "8998f7370973447fac1e8ef6e8ded2c5209a7b1f67c1012866dbcd09681c3251"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:append = " \
    python3-brotli \
    python3-coverage \
    python3-django \
    python3-pytest \
    python3-requests \
"
