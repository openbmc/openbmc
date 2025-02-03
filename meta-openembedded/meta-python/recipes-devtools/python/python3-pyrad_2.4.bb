SUMMARY = "RADIUS tools"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=e910b35b0ef4e1f665b9a75d6afb7709"

PYPI_PACKAGE = "pyrad"
SRC_URI[sha256sum] = "057de4b7e89d8da57ba782c1bde45c63ebee720ae2c0b0a69beaff15c47e30d9"

SRC_URI += "file://use-poetry-core.patch \
            file://208.patch \
            "

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-io \
    python3-logging \
    python3-netaddr \
    python3-six \
"
