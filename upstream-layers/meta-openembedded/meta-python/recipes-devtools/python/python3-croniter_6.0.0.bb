DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "37c504b313956114a983ece2c2b07790b1f1094fe9d81cc94739214748255577"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-dateutil \
	python3-natsort \
	python3-pytz \
"
