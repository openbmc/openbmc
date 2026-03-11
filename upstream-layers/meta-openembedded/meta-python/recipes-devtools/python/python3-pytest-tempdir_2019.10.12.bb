DESCRIPTION = "Adds support for a predictable and repeatable temporary directory."
HOMEPAGE = "https://github.com/saltstack/pytest-tempdir"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=9872c3a37cc9baf79a464cd168282be5"

SRC_URI[sha256sum] = "e7d91813a9aa991db87dacdef8cfd3f1657632d731d56d06238c5ffb63ab36d8"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-pytest \
"
