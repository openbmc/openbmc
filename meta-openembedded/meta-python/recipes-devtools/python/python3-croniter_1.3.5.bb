DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "7592fc0e8a00d82af98dfa2768b75983b6fb4c2adc8f6d0d7c931a715b7cefee"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
