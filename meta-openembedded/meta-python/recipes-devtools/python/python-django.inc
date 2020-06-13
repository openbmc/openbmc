SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

PYPI_PACKAGE = "Django"
inherit pypi

UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

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
    ${PYTHON_PN}-distutils \
"

CVE_PRODUCT = "django"
