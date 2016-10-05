SUMMARY = "Phosphor OpenBMC Event Management"
DESCRIPTION = "Phosphor OpenBMC event management reference implementation."
HOMEPAGE = "https://github.com/openbmc/phosphor-event"
PR = "r1"


inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-c-daemon

PROVIDES += "virtual/obmc-event-mgmt"
RPROVIDES_${PN} += "virtual-obmc-event-mgmt"

TARGET_CXXFLAGS += " -std=c++11 -fpic"
TARGET_CFLAGS += " -fpic"

SRC_URI += "git://github.com/openbmc/phosphor-event"

SRCREV = "acdc2a909e7464111b259fb94509dcf8dab7c626"

RDEPENDS_${PN} += "libsystemd"
DEPENDS += "systemd"

DBUS_SERVICE_${PN} = "org.openbmc.records.events.service"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/eventd/eventd.conf"

S = "${WORKDIR}/git"
INSTALL_NAME = "event_messaged"

do_install() {
        install -d ${D}/var/lib/obmc/events/
        install -m 0755 -d ${D}${sbindir}
        install -m 0755 ${S}/${INSTALL_NAME} ${D}/${sbindir}/phosphor-eventd
}
