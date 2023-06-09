DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "924a38fda88f675ec6835667e1d32ac37ff0d65509c2152729d16ff205e32a65"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
