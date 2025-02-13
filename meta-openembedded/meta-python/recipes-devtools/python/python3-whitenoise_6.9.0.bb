SUMMARY = "Radically simplified static file serving for WSGI applications"
HOMEPAGE = "https://whitenoise.evans.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aba4901cc64e401cea5a267eac2a2e1e"

PYPI_PACKAGE = "whitenoise"

SRC_URI[sha256sum] = "8c4a7c9d384694990c26f3047e118c691557481d624f069b7f7752a2f735d609"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:append = " \
    python3-brotli \
    python3-coverage \
    python3-django \
    python3-pytest \
    python3-requests \
"
