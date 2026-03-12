SUMMARY = "A robust email address syntax and deliverability validation library."
SECTION = "devel/python"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2890aee62bd2a4c3197e2059016a397e"

SRC_URI[sha256sum] = "9fc05c37f2f6cf439ff414f8fc46d917929974a82244c20eb10231ba60c54426"

PYPI_PACKAGE = "email_validator"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-dnspython python3-idna"
