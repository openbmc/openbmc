SUMMARY = "A daemon for delivering ACPI events"
HOMEPAGE = "http://sourceforge.net/projects/acpid/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=33140&atid=407341"
SECTION = "base"
LICENSE = "GPLv2+"

SRC_URI = "${SOURCEFORGE_MIRROR}/acpid2/acpid-${PV}.tar.xz \
           file://init \
           file://acpid.service \
          "

inherit autotools update-rc.d systemd

INITSCRIPT_NAME = "acpid"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "acpid.service"

do_install_append () {
	install -d ${D}${sysconfdir}/init.d
	sed -e 's,/usr/sbin,${sbindir},g' ${WORKDIR}/init > ${D}${sysconfdir}/init.d/acpid
	chmod 755 ${D}${sysconfdir}/init.d/acpid

	install -d ${D}${sysconfdir}/acpi
	install -d ${D}${sysconfdir}/acpi/events

	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/acpid.service ${D}${systemd_unitdir}/system
	sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/acpid.service
}
