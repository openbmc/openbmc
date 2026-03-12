SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dabe659eadd6d97325b1582e41cfc11"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "3fdcaf76f60a1b948ff8e37dc398d03cf9ce7427065d583082b92da7a4ff5a63"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
