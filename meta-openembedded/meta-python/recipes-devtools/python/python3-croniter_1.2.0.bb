DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "094422f6aeb9ed646714393503fa388afe4f846e110e1997fea5794e2085c2d7"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
