SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5dabe659eadd6d97325b1582e41cfc11"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "70758e387fc0e0c4badbcb394f61acbe68b34970a8fed7e0f7c89469fe17912a"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
