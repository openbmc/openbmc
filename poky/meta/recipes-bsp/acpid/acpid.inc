SUMMARY = "A daemon for delivering ACPI events"
DESCRIPTION = "ACPID is a completely flexible, totally extensible daemon for \
delivering ACPI events. It listens on netlink interface (or on the \
deprecated file /proc/acpi/event), and when an event occurs, executes programs \
to handle the event. The programs it executes are configured through a set of \
configuration files, which can be dropped into place by packages or by the \
admin."
HOMEPAGE = "http://sourceforge.net/projects/acpid2"
BUGTRACKER = "http://sourceforge.net/p/acpid2/tickets/?source=navbar"
SECTION = "base"
LICENSE = "GPL-2.0-or-later"

SRC_URI = "${SOURCEFORGE_MIRROR}/acpid2/acpid-${PV}.tar.xz \
           file://init \
           file://acpid.service \
          "

CVE_PRODUCT = "acpid2"

inherit autotools update-rc.d systemd

INITSCRIPT_NAME = "acpid"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE:${PN} = "acpid.service"

do_install:append () {
	install -d ${D}${sysconfdir}/init.d
	sed -e 's,/usr/sbin,${sbindir},g' ${WORKDIR}/init > ${D}${sysconfdir}/init.d/acpid
	chmod 755 ${D}${sysconfdir}/init.d/acpid

	install -d ${D}${sysconfdir}/acpi
	install -d ${D}${sysconfdir}/acpi/events

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/acpid.service ${D}${systemd_system_unitdir}
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_system_unitdir}/acpid.service
}
