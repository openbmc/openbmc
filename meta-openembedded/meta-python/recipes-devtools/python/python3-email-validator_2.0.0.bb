SUMMARY = "A robust email address syntax and deliverability validation library."
SECTION = "devel/python"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d3616852dbf7b1a6d4b53b00626032"

SRC_URI[sha256sum] = "f4904f4145c11f8de5897afbb8d7db4b465c57f13db1c8c106c16d02bceebd9a"

PYPI_PACKAGE = "email_validator"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-dnspython python3-idna"
