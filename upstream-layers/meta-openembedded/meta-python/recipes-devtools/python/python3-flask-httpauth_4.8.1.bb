SUMMARY = "Simple extension that provides Basic and Digest HTTP authentication for Flask routes."
HOMEPAGE = "https://github.com/miguelgrinberg/flask-httpauth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "88499b22f1353893743c3cd68f2ca561c4ad9ef75cd6bcc7f621161cd0e80744"

RDEPENDS:${PN} += "\
    python3-flask \
    "
