SUMMARY = "A decorator for caching properties in classes."
DESCRIPTION = "Makes caching of time or computational expensive properties quick and easy."
HOMEPAGE = "https://pypi.org/project/cached-property/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db7ff60c4e14f58534201242803d8abc"

SRC_URI[sha256sum] = "9fa5755838eecbb2d234c3aa390bd80fbd3ac6b6869109bfc1b499f7bd89a130"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
