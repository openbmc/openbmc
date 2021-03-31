DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "364c48e393060295c3161588a6556d5c890b5c34299973c393adbe4488ca1ecb"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
