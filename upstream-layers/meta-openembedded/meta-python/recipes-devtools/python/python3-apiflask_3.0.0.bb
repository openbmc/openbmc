SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "2b8420a23b93a78407ef5fe3e1f79cdfc7baa9b3c53977a06d6ab68772a7c2b7"

RDEPENDS:${PN} += "\
    python3-apispec \
    python3-flask \
    python3-flask-httpauth \
    python3-flask-marshmallow \
    python3-webargs \
    "
