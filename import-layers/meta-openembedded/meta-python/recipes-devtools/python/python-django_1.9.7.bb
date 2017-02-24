SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI[md5sum] = "7de9ba83bfe01f4b7d45645c1b259c83"
SRC_URI[sha256sum] = "2b29e81c8c32b3c0d9a0119217416887c480d927ae2630bada2da83078c93bf6"

PYPI_PACKAGE = "Django"
inherit pypi setuptools

FILES_${PN} += "${datadir}/django"

BBCLASSEXTEND = "nativesdk"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-importlib \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pip \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
    "
