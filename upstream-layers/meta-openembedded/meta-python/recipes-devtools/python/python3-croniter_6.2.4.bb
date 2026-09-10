DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

SRC_URI += "file://0001-Allow-using-newer-versions-of-trove-classifiers-and-.patch"
SRC_URI[sha256sum] = "fc124f751b1b04805c2a04b061898b436b45ab2320b045e1e052ea895de65189"

inherit pypi python_hatchling

DEPENDS += "python3-pathspec python3-trove-classifiers"
RDEPENDS:${PN} += " \
	python3-dateutil \
	python3-natsort \
	python3-pytz \
"
