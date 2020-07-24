DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"
SRC_URI[md5sum] = "adfeeb0032ab7aeccba908fa2eb09c33"
SRC_URI[sha256sum] = "7186b9b464f45cf3d3c83a18bc2344cc101d7b9fd35a05f2878437b14967e964"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
