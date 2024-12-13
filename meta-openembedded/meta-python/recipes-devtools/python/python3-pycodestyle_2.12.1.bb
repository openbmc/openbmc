SUMMARY = "Python style guide checker (formly called pep8)"
HOMEPAGE = "https://pypi.org/project/pycodestyle"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8546d0e77f416fb05a26acd89c8b3bd"

SRC_URI[sha256sum] = "6838eae08bbce4f6accd5d5572075c63626a15ee3e6f842df996bf62f6d73521"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
