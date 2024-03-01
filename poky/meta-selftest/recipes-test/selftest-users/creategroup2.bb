SUMMARY = "creategroup pt 2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

USERADD_DEPENDS = "creategroup1"

S = "${WORKDIR}"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5556 --gid grouptest gt2"

TESTDIR = "${D}${sysconfdir}/creategroup"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    gt2:grouptest ${TESTDIR}/file
	chown -R gt2:grouptest ${TESTDIR}/dir
	chown -h gt2:grouptest ${TESTDIR}/symlink
	chown -R gt2:grouptest ${TESTDIR}/fifotest
}

FILES:${PN} = "${sysconfdir}/creategroup/*"

