SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dabe659eadd6d97325b1582e41cfc11"

PYPI_PACKAGE = "python_gnupg"
SRC_URI[sha256sum] = "5743e96212d38923fc19083812dc127907e44dbd3bcf0db4d657e291d3c21eac"

CVE_PRODUCT = "python:python-gnupg"

inherit pypi python_setuptools_build_meta

S = "${UNPACKDIR}/python-gnupg-${PV}"

DEPENDS += "python3-wheel-native"

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
