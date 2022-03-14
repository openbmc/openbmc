SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b0b7ac63b60085b23fa9f7e1951daa1d"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "b64de1ae5cedf872b437201a566fa2c62ce0c95ea2e30177eb53aee1258507d7"

inherit pypi setuptools3

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
