SUMMARY = "creategroup pt 1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5555 --gid grouptest gt1"
GROUPADD_PARAM:${PN} = "-r grouptest"

TESTDIR = "${D}${sysconfdir}/creategroup"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    gt1:grouptest ${TESTDIR}/file
	chown -R gt1:grouptest ${TESTDIR}/dir
	chown -h gt1:grouptest ${TESTDIR}/symlink
	chown -R gt1:grouptest ${TESTDIR}/fifotest
}

FILES:${PN} = "${sysconfdir}/creategroup/*"
