DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "72ef78d0f8337eb35393b8893ebfbfbeb340f2d2ae47e0d2d78130e34b0dd8b9"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
