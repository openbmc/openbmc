SUMMARY = "A robust email address syntax and deliverability validation library."
SECTION = "devel/python"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d3616852dbf7b1a6d4b53b00626032"

SRC_URI[md5sum] = "25582cf3d415c7fdd44b980a095e0d53"
SRC_URI[sha256sum] = "aa237a65f6f4da067119b7df3f13e89c25c051327b2b5b66dc075f33d62480d7"

PYPI_PACKAGE = "email_validator"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-dnspython python3-idna"
