require samhain.inc

SRC_URI += "file://samhain-not-run-ptest-on-host.patch \
            file://run-ptest \
"

PROVIDES += "samhain"

SYSTEMD_SERVICE_${PN} = "samhain.service"

inherit ptest

do_compile() {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'yes', 'no', d)}" = "yes" ]; then
		oe_runmake cutest
		rm -f ${S}*.o config_xor.h internal.h
	fi
	oe_runmake "$@"
}

do_install_append() {
    ln -sf ${INITSCRIPT_NAME} ${D}${sysconfdir}/init.d/samhain
}

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}
	install ${S}/cutest ${D}${PTEST_PATH}
}

RPROVIDES_${PN} += "samhain"
RCONFLICTS_${PN} = "samhain-client samhain-server"
