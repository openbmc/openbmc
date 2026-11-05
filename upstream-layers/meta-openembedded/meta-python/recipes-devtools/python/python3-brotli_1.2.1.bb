SUMMARY = "Brotli compression format"
HOMEPAGE = "https://pypi.org/project/Brotli/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=941ee9cd1609382f946352712a319b4b"

PYPI_PACKAGE = "brotli"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

CVE_PRODUCT = "brotli"

SRC_URI[sha256sum] = "02b2f7b5a83485efe448a44ddf7983def05c3bb1dcf917d4f7154070260e13de"

inherit pypi pkgconfig python_setuptools_build_meta

DEPENDS += " \
	python3-pkgconfig-native \
"

RDEPENDS:${PN} += " \
	python3-cffi \
"

BBCLASSEXTEND = "native nativesdk"
