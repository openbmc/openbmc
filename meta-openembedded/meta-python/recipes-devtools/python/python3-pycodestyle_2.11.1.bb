SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "41ba0e7afc9752dfb53ced5489e89f8186be00e599e712660695b7a75ff2663f"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
