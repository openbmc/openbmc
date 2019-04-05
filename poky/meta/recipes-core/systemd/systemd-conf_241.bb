require systemd.inc

SUMMARY = "Systemd system configuration"
DESCRIPTION = "Systemd may require slightly different configuration for \
different machines.  For example, qemu machines require a longer \
DefaultTimeoutStartSec setting."

PACKAGE_ARCH = "${MACHINE_ARCH}"

CONFFILES_${PN} = "${sysconfdir}/machine-id \
${sysconfdir}/systemd/coredump.conf \
${sysconfdir}/systemd/journald.conf \
${sysconfdir}/systemd/logind.conf \
${sysconfdir}/systemd/system.conf \
${sysconfdir}/systemd/user.conf"

FILES_${PN} = "${sysconfdir}/machine-id ${sysconfdir}/systemd"

do_configure[noexec] = '1'
do_compile[noexec] = '1'

do_install() {
	rm -rf ${D}/${sysconfdir}/systemd
	install -d ${D}/${sysconfdir}/systemd

	# Create machine-id
	# 20:12 < mezcalero> koen: you have three options: a) run systemd-machine-id-setup at install time, b) have / read-only and an empty file there (for stateless) and c) boot with / writable
	touch ${D}${sysconfdir}/machine-id

	install -m 0644 ${S}/src/coredump/coredump.conf ${D}${sysconfdir}/systemd/coredump.conf

	install -m 0644 ${S}/src/journal/journald.conf ${D}${sysconfdir}/systemd/journald.conf
	# Enable journal to forward message to syslog daemon
	sed -i -e 's/.*ForwardToSyslog.*/ForwardToSyslog=yes/' ${D}${sysconfdir}/systemd/journald.conf
	# Set the maximium size of runtime journal to 64M as default
	sed -i -e 's/.*RuntimeMaxUse.*/RuntimeMaxUse=64M/' ${D}${sysconfdir}/systemd/journald.conf

	install -m 0644 ${S}/src/login/logind.conf.in ${D}${sysconfdir}/systemd/logind.conf
	# Set KILL_USER_PROCESSES to yes
	sed -i -e 's/@KILL_USER_PROCESSES@/yes/' ${D}${sysconfdir}/systemd/logind.conf

	install -m 0644 ${S}/src/core/system.conf.in ${D}${sysconfdir}/systemd/system.conf
	# Set MEMORY_ACCOUNTING_DEFAULT to yes
	sed -i -e 's/@MEMORY_ACCOUNTING_DEFAULT@/yes/' ${D}${sysconfdir}/systemd/system.conf

	install -m 0644 ${S}/src/core/user.conf ${D}${sysconfdir}/systemd/user.conf
}

# Based on change from YP bug 8141, OE commit 5196d7bacaef1076c361adaa2867be31759c1b52
do_install_append_qemuall() {
	# Change DefaultTimeoutStartSec from 90s to 240s
	echo "DefaultTimeoutStartSec = 240s" >> ${D}${sysconfdir}/systemd/system.conf
}
