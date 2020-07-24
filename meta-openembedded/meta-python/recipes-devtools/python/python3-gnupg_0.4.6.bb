SUMMARY = "A wrapper for the Gnu Privacy Guard (GPG or GnuPG)"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b0b7ac63b60085b23fa9f7e1951daa1d"

PYPI_PACKAGE = "python-gnupg"
SRC_URI[sha256sum] = "3aa0884b3bd414652c2385b9df39e7b87272c2eca1b8fcc3089bc9e58652019a"

inherit pypi setuptools3

RDEPENDS_${PN} +=  "gnupg-gpg"
