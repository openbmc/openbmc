DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "12ced475dfc107bf7c6c1440af031f34be14cd97bbbfaf0f62221a9c11e86404"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
