DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "32a5ec04e97ec0837bcdf013767abd2e71cceeefd3c2e14c804098ce51ad6cd9"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
