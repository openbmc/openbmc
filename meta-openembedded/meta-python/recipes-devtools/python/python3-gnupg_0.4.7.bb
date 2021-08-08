SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b0b7ac63b60085b23fa9f7e1951daa1d"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "2061f56b1942c29b92727bf9aecbd3cea3893acc9cccbdc7eb4604285efe4ac7"

inherit pypi setuptools3

RDEPENDS:${PN} +=  " \
	gnupg-gpg \
	python3-logging \
"
