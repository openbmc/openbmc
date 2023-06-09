SUMMARY = "A one step database access tool, built on the SQLAlchemy ORM."
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4382f3a1adb96f258dbd80f5b400f0d5"

PYPI_PACKAGE = "sqlsoup"
SRC_URI[sha256sum] = "2fafb7732a663dcd59b37e64d1c94d5fb20d4fad32cd8ee260aa1cd9a10340d6"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-sqlalchemy"
