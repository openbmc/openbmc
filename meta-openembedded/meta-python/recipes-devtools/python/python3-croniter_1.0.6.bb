DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "e79bcc9681d2345e71360241aebe19ed6c5475fec40cc59a7998fe1a2ca568d0"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
