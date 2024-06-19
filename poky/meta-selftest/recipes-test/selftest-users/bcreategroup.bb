SUMMARY = "creategroup_b"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

# This recipe requires a and c. C requires A. Reverse alpha.

USERADD_DEPENDS = "acreategroup ccreategroup"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

EXCLUDE_FROM_WORLD="1"

inherit useradd allarch

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-u 5561 -g b_group -G a_group,c_group b_user "
GROUPADD_PARAM:${PN} = "-r b_group"

TESTDIR = "${D}${sysconfdir}/creategroup"

do_install() {
	install -d   ${TESTDIR}
	install -d   ${TESTDIR}/dir
	touch        ${TESTDIR}/file
	ln -s ./file ${TESTDIR}/symlink
	install -d   ${TESTDIR}/fifotest
	mkfifo       ${TESTDIR}/fifotest/fifo

	chown    a_user:a_group ${TESTDIR}/file
	chown -R c_user:c_group ${TESTDIR}/dir
	chown -h a_user:a_group ${TESTDIR}/symlink
	chown -R b_user:b_group ${TESTDIR}/fifotest
}

FILES:${PN} = "${sysconfdir}/creategroup/*"

