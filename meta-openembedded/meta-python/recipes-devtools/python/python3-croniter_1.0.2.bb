DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "12f9fd52e7cfb623d0788956d137a3de26e5583a25820fa7710fb6b58d32b213"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
