SUMMARY = "creategroup_c"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

USERADD_DEPENDS = "acreategroup"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

EXCLUDE_FROM_WORLD="1"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5563 --gid c_group -G a_group c_user"
GROUPADD_PARAM:${PN} = "-r c_group"

TESTDIR = "${D}${sysconfdir}/creategroup"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    c_user:c_group ${TESTDIR}/file
	chown -R c_user:c_group ${TESTDIR}/dir
	chown -h c_user:c_group ${TESTDIR}/symlink
	chown -R c_user:c_group ${TESTDIR}/fifotest
}

FILES:${PN} = "${sysconfdir}/creategroup/*"
