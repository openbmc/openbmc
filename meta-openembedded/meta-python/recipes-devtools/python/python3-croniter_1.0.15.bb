DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "a70dfc9d52de9fc1a886128b9148c89dd9e76b67d55f46516ca94d2d73d58219"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
