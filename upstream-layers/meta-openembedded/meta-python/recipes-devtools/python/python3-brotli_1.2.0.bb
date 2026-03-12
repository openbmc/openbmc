SUMMARY = "Brotli compression format"
HOMEPAGE = "https://pypi.org/project/Brotli/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=941ee9cd1609382f946352712a319b4b"

PYPI_PACKAGE = "brotli"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

CVE_PRODUCT = "brotli"

SRC_URI[sha256sum] = "e310f77e41941c13340a95976fe66a8a95b01e783d430eeaf7a2f87e0a57dd0a"

inherit pypi pkgconfig python_setuptools_build_meta

DEPENDS += " \
	python3-pkgconfig-native \
"

RDEPENDS:${PN} += " \
	python3-cffi \
"

BBCLASSEXTEND = "native nativesdk"
