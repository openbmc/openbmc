SUMMARY = "Bleak is a GATT client software, capable of connecting to BLE devices acting as GATT servers."
HOMEPAGE = "https://github.com/hbldh/bleak"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcbc2069a86cba1b5e47253679f66ed7"

SRCREV = "054b092e19c7a45d5ea07d8948022f54117fe5e8"
PYPI_SRC_URI = "git://github.com/hbldh/bleak.git;protocol=https;branch=develop"

inherit pypi python_poetry_core ptest

S = "${WORKDIR}/git"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-pytest-asyncio \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += " \
	python3-core (>3.7) \
	python3-async-timeout \
	python3-dbus-fast \
	python3-xml \
"
