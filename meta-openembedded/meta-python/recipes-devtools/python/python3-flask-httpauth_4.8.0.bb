SUMMARY = "Simple extension that provides Basic and Digest HTTP authentication for Flask routes."
HOMEPAGE = "https://github.com/miguelgrinberg/flask-httpauth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "Flask-HTTPAuth"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "66568a05bc73942c65f1e2201ae746295816dc009edd84b482c44c758d75097a"

RDEPENDS:${PN} += "\
    python3-flask \
    "
