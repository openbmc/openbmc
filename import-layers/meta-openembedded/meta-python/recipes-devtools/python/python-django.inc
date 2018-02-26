SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI[md5sum] = "8cef0d42aabacbc414ec4fbbb6056f3c"
SRC_URI[sha256sum] = "1836878162dfdf865492bacfdff0321e4ee8f1e7d51d93192546000b54982b29"

PYPI_PACKAGE = "Django"
inherit pypi

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
