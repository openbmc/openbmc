SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "b7894741cdea90013276873cdf635d5150e1deb3fe20d02d429866af960f275f"

RDEPENDS:${PN} += "\
    python3-apispec \
    python3-flask \
    python3-flask-httpauth \
    python3-flask-marshmallow \
    python3-webargs \
    "
