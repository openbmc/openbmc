SUMMARY = "Radically simplified static file serving for WSGI applications"
HOMEPAGE = "https://whitenoise.evans.io"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aba4901cc64e401cea5a267eac2a2e1e"

PYPI_PACKAGE = "whitenoise"

SRC_URI[sha256sum] = "f723ebb76a112e98816ff80fcea0a6c9b8ecde835f8ddda25df7a30a3c2db6ad"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:append = " \
    python3-brotli \
    python3-coverage \
    python3-django \
    python3-pytest \
    python3-requests \
"
