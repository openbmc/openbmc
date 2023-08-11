SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dabe659eadd6d97325b1582e41cfc11"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "5674bad4e93876c0b0d3197e314d7f942d39018bf31e2b833f6788a6813c3fb8"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
