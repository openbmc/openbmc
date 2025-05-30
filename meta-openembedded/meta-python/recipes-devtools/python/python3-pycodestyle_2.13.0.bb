SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "c8415bf09abe81d9c7f872502a6eee881fbe85d8763dd5b9924bb0a01d67efae"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
