SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

SRC_URI[md5sum] = "38e82b59a1c27bbf98ccf0564ead7426"
SRC_URI[sha256sum] = "eb9271f0874f53106a2719c0c35ce67631f6cc27cf81a60c6f8c9817b35a3f6e"

PYPI_PACKAGE = "Django"
inherit pypi

UPSTREAM_CHECK_REGEX = "(?P<pver>1(\.\d+)+)"

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
