SUMMARY = "creategroup_a"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

EXCLUDE_FROM_WORLD = "1"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5560 --gid a_group a_user"
GROUPADD_PARAM:${PN} = "-r a_group"

TESTDIR = "${D}${sysconfdir}/creategroup"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    a_user:a_group ${TESTDIR}/file
	chown -R a_user:a_group ${TESTDIR}/dir
	chown -h a_user:a_group ${TESTDIR}/symlink
	chown -R a_user:a_group ${TESTDIR}/fifotest
}

FILES:${PN} = "${sysconfdir}/creategroup/*"
