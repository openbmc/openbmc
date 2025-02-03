SUMMARY = "A decorator for caching properties in classes."
DESCRIPTION = "Makes caching of time or computational expensive properties quick and easy."
HOMEPAGE = "https://pypi.org/project/cached-property/"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db7ff60c4e14f58534201242803d8abc"

PYPI_PACKAGE = "cached_property"
SRC_URI[sha256sum] = "484d617105e3ee0e4f1f58725e72a8ef9e93deee462222dbd51cd91230897641"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
