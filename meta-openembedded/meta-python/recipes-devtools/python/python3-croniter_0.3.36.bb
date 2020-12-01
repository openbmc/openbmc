DESCRIPTION = "croniter provides iteration for datetime object with cron like format"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=b8ee59850b882cbf623188489ea748e2"

PYPI_PACKAGE = "croniter"
SRC_URI[md5sum] = "1be5dd43ca26a66b5f981e41c74fd346"
SRC_URI[sha256sum] = "9d3098e50f7edc7480470455d42f09c501fa1bb7e2fc113526ec6e90b068f32c"

inherit pypi setuptools3

RDEPENDS_${PN} += " python3-dateutil python3-natsort"
