SUMMARY = "OpenBMC console daemon"
DESCRIPTION = "Daemon to handle UART console connections"
HOMEPAGE = "http://github.com/openbmc/obmc-console"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit autotools

TARGET_CFLAGS   += "-fpic -O2"

SRC_URI += "git://github.com/openbmc/obmc-console"
SRC_URI += "file://${PN}.conf \
	    file://obmc-console-ssh.socket \
	    file://obmc-console-ssh@.service"

SRCREV = "bc1e893375e4887ef7676c5738779c4a2f5b1935"

FILES_${PN} += "${systemd_unitdir}/system/obmc-console-ssh@.service \
		${systemd_unitdir}/system/obmc-console-ssh.socket"

SYSTEMD_SERVICE_${PN} = "${BPN}.service ${BPN}-ssh.socket"

do_install_append() {
        install -m 0755 -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/${PN}.conf ${D}${sysconfdir}/${PN}.conf

	# add additional unit files for ssh-based console server
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/obmc-console-ssh@.service ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/obmc-console-ssh.socket ${D}${systemd_unitdir}/system
	sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
		-e 's,@BINDIR@,${bindir},g' \
		-e 's,@SBINDIR@,${sbindir},g' \
		${D}${systemd_unitdir}/system/obmc-console-ssh@.service \
		${D}${systemd_unitdir}/system/obmc-console-ssh.socket
}

S = "${WORKDIR}/git"
