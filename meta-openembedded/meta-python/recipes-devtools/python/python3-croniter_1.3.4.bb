DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"

SRC_URI[sha256sum] = "3169365916834be654c2cac57ea14d710e742f8eb8a5fce804f6ce548da80bf2"

inherit pypi setuptools3

RDEPENDS:${PN} += " python3-dateutil python3-natsort"
