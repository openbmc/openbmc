SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dabe659eadd6d97325b1582e41cfc11"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "f2fdb5fb29615c77c2743e1cb3d9314353a6e87b10c37d238d91ae1c6feae086"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
