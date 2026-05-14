SUMMARY = "Test recipe that only uses USERADD_DEPENDS without USERADD_PARAM"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

LICENSE = "MIT"

# This recipe depends on the user/group created by creategroup1
# but does NOT create users or groups itself.
USERADD_DEPENDS = "creategroup1"

EXCLUDE_FROM_WORLD = "1"

inherit useradd allarch

TESTDIR = "${D}${sysconfdir}/deponly"

do_install() {
	install -d   ${TESTDIR}
	touch        ${TESTDIR}/file
	chown gt1:grouptest ${TESTDIR}/file
}

FILES:${PN} = "${sysconfdir}/deponly/*"
