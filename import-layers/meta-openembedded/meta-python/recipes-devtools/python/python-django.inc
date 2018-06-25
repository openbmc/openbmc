SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI[md5sum] = "f306015e16a8d5024dbac923ac34fffb"
SRC_URI[sha256sum] = "22383567385a9c406d8a5ce080a2694c82c6b733e157922197e8b393bb3aacd9"

PYPI_PACKAGE = "Django"
inherit pypi

FILES_${PN} += "${datadir}/django"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-email \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-pytz \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-unixadmin \
    ${PYTHON_PN}-xml \
"
