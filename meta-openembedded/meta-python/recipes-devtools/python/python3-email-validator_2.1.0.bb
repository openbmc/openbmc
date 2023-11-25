SUMMARY = "A robust email address syntax and deliverability validation library."
SECTION = "devel/python"
LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=65d3616852dbf7b1a6d4b53b00626032"

SRC_URI[sha256sum] = "5f511cca8856bb03251d6292ba59e7f98978aae13fa5823ddd8bf885c56a6260"

PYPI_PACKAGE = "email_validator"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-dnspython python3-idna"
