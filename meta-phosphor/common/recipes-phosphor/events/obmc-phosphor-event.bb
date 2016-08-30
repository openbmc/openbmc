SUMMARY = "Phosphor OpenBMC Event Management"
DESCRIPTION = "Phosphor OpenBMC event management reference implementation."
HOMEPAGE = "https://github.com/openbmc/phosphor-event"
PR = "r1"


inherit obmc-phosphor-license
inherit obmc-phosphor-event-mgmt
inherit obmc-phosphor-dbus-service
inherit obmc-phosphor-c-daemon

TARGET_CXXFLAGS += " -std=c++11 -fpic"
TARGET_CFLAGS += " -fpic"

SRC_URI += "git://github.com/openbmc/phosphor-event"

SRCREV = "059b35e1eabdd87ad02b4343692789386c134082"

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
