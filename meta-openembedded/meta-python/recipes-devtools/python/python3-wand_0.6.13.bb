SUMMARY = "Ctypes-based simple MagickWand API binding for Python"
HOMEPAGE = "https://docs.wand-py.org/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0bf19e5c00d047fee994ae332db3aab6"

SRC_URI[sha256sum] = "f5013484eaf7a20eb22d1821aaefe60b50cc329722372b5f8565d46d4aaafcca"

inherit pypi setuptools3

PYPI_PACKAGE="Wand"

FILES:${PN}-doc += "${datadir}/README.rst"

BBCLASSEXTEND = "native nativesdk"
