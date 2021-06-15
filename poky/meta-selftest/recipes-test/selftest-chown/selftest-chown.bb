SUMMARY = "selftest chown"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

DEPENDS += "coreutils-native"

S = "${WORKDIR}"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-u 1234 -M test"
TESTDIR = "${D}${sysconfdir}/selftest-chown"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    test:test ${TESTDIR}/file
	chown -R test:test ${TESTDIR}/dir
	chown -h test:test ${TESTDIR}/symlink
	chown -R test:test ${TESTDIR}/fifotest
}

FILES_${PN} = "${sysconfdir}/selftest-chown/*"
