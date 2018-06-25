inherit ptest

FILESEXTRAPATHS_prepend := "${COREBASE}/meta/files:"

SRC_URI += "file://ptest-perl/run-ptest"

do_install_ptest_perl() {
	install -d ${D}${PTEST_PATH}
	if [ ! -f ${D}${PTEST_PATH}/run-ptest ]; then
		install -m 0755 ${WORKDIR}/ptest-perl/run-ptest ${D}${PTEST_PATH}
	fi
	cp -r ${B}/t ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}
}

FILES_${PN}-ptest_prepend = "${PTEST_PATH}/t/* ${PTEST_PATH}/run-ptest "

RDEPENDS_${PN}-ptest_prepend = "perl "

addtask install_ptest_perl after do_install_ptest_base before do_package

python () {
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        d.setVarFlag('do_install_ptest_perl', 'fakeroot', '1')

    # Remove all '*ptest_perl' tasks when ptest is not enabled
    if not(d.getVar('PTEST_ENABLED') == "1"):
        for i in ['do_install_ptest_perl']:
            bb.build.deltask(i, d)
}
