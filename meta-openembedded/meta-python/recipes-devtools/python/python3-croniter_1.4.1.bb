DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "1a6df60eacec3b7a0aa52a8f2ef251ae3dd2a7c7c8b9874e73e791636d55a361"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
