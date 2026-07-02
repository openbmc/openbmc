SUMMARY = "APIFlask is a lightweight Python web API framework based on Flask and marshmallow-code projects."
HOMEPAGE = "https://github.com/apiflask/apiflask"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5f89d1b0dec37448d4f4163dc3c40e64"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "f3b367d4174d8ad1df9ac3a48a63002276e9f82f7debda91314cca7a20924ada"

RDEPENDS:${PN} += "\
    python3-apispec \
    python3-flask \
    python3-flask-httpauth \
    python3-flask-marshmallow \
    python3-webargs \
    "
