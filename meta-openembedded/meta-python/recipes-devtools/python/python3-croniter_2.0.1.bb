DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "d199b2ec3ea5e82988d1f72022433c5f9302b3b3ea9e6bfd6a1518f6ea5e700a"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
