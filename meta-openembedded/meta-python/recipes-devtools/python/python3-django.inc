SUMMARY = "A high-level Python Web framework"
HOMEPAGE = "http://www.djangoproject.com/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f09eb47206614a4954c51db8a94840fa"

PYPI_PACKAGE = "Django"
inherit pypi

UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

FILES:${PN} += "${datadir}/django"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "\
    python3-compression \
    python3-ctypes \
    python3-datetime \
    python3-email \
    python3-html \
    python3-json \
    python3-logging \
    python3-multiprocessing \
    python3-netserver \
    python3-numbers \
    python3-pkgutil \
    python3-pytz \
    python3-threading \
    python3-unixadmin \
    python3-xml \
"

CVE_PRODUCT = "django"
