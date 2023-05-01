DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "d067b1f95b553c6e82d95a983c465695913dcd12f47a8b9aa938a0450d94dd5e"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
