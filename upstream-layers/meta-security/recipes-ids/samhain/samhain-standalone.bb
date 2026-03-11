require samhain.inc

SRC_URI += "file://0011-not-run-test-on-host-since-we-are-doing-cross-compil.patch \
            file://0012-Don-t-expose-configure-args.patch \
            file://run-ptest \
"

PROVIDES += "samhain"

MODE_NAME = "standalone"
SAMHAIN_MODE = "no"

SYSTEMD_SERVICE:${PN} = "samhain.service"

inherit ptest

do_compile() {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'yes', 'no', d)}" = "yes" ]; then
		oe_runmake cutest
		rm -f ${S}*.o config_xor.h internal.h
	fi
	oe_runmake "$@"
}

do_install:append() {
    ln -sf ${INITSCRIPT_NAME} ${D}${sysconfdir}/init.d/samhain
}

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}
	install -m 755 ${S}/cutest ${D}${PTEST_PATH}
	install -m 644 ${S}/COPYING ${D}${PTEST_PATH}
}

RPROVIDES:${PN} += "samhain"
RCONFLICTS:${PN} = "samhain-client samhain-server"
