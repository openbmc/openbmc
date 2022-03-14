SUMMARY = "Python library to talk to the met.no api"
HOMEPAGE = "https://github.com/Danielhiversen/pyMetno"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d503272f52c35147ec960cb56a03bf4"

SRC_URI = "git://github.com/Danielhiversen/pyMetno.git;protocol=https;branch=master"
SRCREV = "3b18971fb882deaaebb9aa511627c5fc6fb97526"
S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} = "\
	${PYTHON_PN}-aiohttp (>=3.6.1) \
	${PYTHON_PN}-async-timeout (>=3.0.1) \
	${PYTHON_PN}-pytz (>=2019.3) \
"
