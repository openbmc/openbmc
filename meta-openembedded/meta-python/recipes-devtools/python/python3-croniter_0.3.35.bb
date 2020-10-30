DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"
SRC_URI[md5sum] = "8bb1443b90123f96ad64d7fae26df342"
SRC_URI[sha256sum] = "b9075573d9d18fdc4c67ad6741c4bfa4b446b1b1d7f03279757244c8a75abedf"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
