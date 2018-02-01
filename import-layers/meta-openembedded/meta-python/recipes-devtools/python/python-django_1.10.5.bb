SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI[md5sum] = "3fce02f1e6461fec21f1f15ea7489924"
SRC_URI[sha256sum] = "0db89374b691b9c8b057632a6cd64b18d08db2f4d63b4d4af6024267ab965f8b"

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
