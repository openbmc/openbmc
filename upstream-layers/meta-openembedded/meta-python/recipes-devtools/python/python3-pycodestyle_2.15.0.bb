SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "318f5db083869b4c4dad922d0b11124fb27ab181b6730b93371da671e31bd50e"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
