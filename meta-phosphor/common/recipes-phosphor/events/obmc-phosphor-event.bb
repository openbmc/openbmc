SUMMARY = "Phosphor OpenBMC Event Management"
DESCRIPTION = "Phosphor OpenBMC event management reference implementation."
HOMEPAGE = "https://github.com/openbmc/phosphor-event"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
PROVIDES += "virtual/obmc-event-mgmt"
RPROVIDES_${PN} += "virtual-obmc-event-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-event"

SRCREV = "c6861bec27c1c2e971f8be92bdf5495f4f4b9668"

DBUS_SERVICE_${PN} = "org.openbmc.records.events.service"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/eventd/eventd.conf"

S = "${WORKDIR}/git"

do_install() {
        install -d ${D}/var/lib/obmc/events/
}
