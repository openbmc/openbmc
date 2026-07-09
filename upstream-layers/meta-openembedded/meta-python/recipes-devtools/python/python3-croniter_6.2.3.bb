DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

SRC_URI += "file://0001-Allow-using-newer-versions-of-trove-classifiers-and-.patch"
SRC_URI[sha256sum] = "fb129986ef7e2c44e3f4c9f503da83ad914d2afa48f40a43ee3dca4b5c41d476"

inherit pypi python_hatchling

DEPENDS += "python3-pathspec python3-trove-classifiers"
RDEPENDS:${PN} += " \
	python3-dateutil \
	python3-natsort \
	python3-pytz \
"
