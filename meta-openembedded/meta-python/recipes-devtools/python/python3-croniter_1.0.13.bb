DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "57f36df4f5a1c31bbf50bbffa3551612c19f6156ab1e88e2e42342ccbb9f9b9a"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
