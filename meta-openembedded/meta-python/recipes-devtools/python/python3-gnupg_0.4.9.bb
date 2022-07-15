SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dabe659eadd6d97325b1582e41cfc11"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "aaa748795572591aaf127b4ac8985684f3673ff82b39f370c836b006e68fc537"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
