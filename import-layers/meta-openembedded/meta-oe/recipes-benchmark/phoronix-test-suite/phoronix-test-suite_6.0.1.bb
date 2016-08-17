SUMMARY = "Phoronix Test Suite"
DESCRIPTION = "The Phoronix Test Suite is designed to carry out both qualitative \
and quantitative benchmarks in a clean, reproducible, and easy-to-use manner."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "console/tests"

SRC_URI = "http://www.phoronix-test-suite.com/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "c3b26fcc57a3a253e558f759fdc1089f"
SRC_URI[sha256sum] = "27add54f4ecb464549de580cece84b4a4945b99df3ef7ff7034eb7f23ffb3b39"
S = "${WORKDIR}/phoronix-test-suite"

inherit systemd allarch

do_install() {
	DESTDIR=${D} ./install-sh ${exec_prefix}

	if [ "${systemd_unitdir}" != "/usr/lib/systemd" ]; then
		install -d ${D}/${systemd_unitdir}/system/
		mv ${D}/usr/lib/systemd/system/* ${D}/${systemd_unitdir}/system/
		rm -rf ${D}/usr/lib/
	fi
}

SYSTEMD_SERVICE_${PN} = "phoromatic-client.service phoromatic-server.service"
RDEPENDS_${PN} += "bash python php-cli"

FILES_${PN} += " \
	${datadir}/phoronix-test-suite \
	${datadir}/appdata/phoronix-test-suite.appdata.xml \
	${datadir}/icons/hicolor/48x48/apps/phoronix-test-suite.png \
	${datadir}/icons/hicolor/64x64/mimetypes/application-x-openbenchmarking.png \
	${datadir}/mime/packages/openbenchmarking-mime.xml \
	${systemd_unitdir}/* \
"
