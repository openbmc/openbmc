SUMMARY = "creategroup_d"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

USERADD_DEPENDS = "bcreategroup"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

EXCLUDE_FROM_WORLD="1"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5564 -g d_group -G b_group d_user "
GROUPADD_PARAM:${PN} = "-r d_group"

TESTDIR = "${D}${sysconfdir}/creategroup"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    d_user:d_group ${TESTDIR}/file
	chown -R d_user:b_group ${TESTDIR}/dir
	chown -h d_user:d_group ${TESTDIR}/symlink
	chown -R d_user:b_group ${TESTDIR}/fifotest
}

