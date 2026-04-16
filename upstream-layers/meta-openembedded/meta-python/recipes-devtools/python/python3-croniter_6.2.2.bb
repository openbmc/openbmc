DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "ba60832a5ec8e12e51b8691c3309a113d1cf6526bdf1a48150ce8ec7a532d0ab"

inherit pypi python_hatchling

RDEPENDS:${PN} += " \
	python3-dateutil \
	python3-natsort \
	python3-pytz \
"
