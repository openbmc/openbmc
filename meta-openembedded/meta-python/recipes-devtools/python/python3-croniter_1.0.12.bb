DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "3603ef0d60fc6df98ce356c01529de90b06a002379b9f5e9eae981fb9c1fd936"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
