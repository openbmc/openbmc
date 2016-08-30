SUMMARY = "Phosphor OpenBMC Event Management"
DESCRIPTION = "Phosphor OpenBMC event management reference implementation."
HOMEPAGE = "https://github.com/openbmc/phosphor-event"
PR = "r1"


inherit obmc-phosphor-license
inherit obmc-phosphor-event-mgmt
inherit obmc-phosphor-sdbus-service
inherit obmc-phosphor-c-daemon

TARGET_CXXFLAGS += "-std=c++11 -fpic"
TARGET_CFLAGS += " -fpic"

SRC_URI += "git://github.com/openbmc/phosphor-event"
SRC_URI += "file://eventd.conf"

SRCREV = "4dad23916e69d55d692eca7389d67eb023c5ca66"

RDEPENDS_${PN} += "libsystemd"
DEPENDS += "systemd"


S = "${WORKDIR}/git"
INSTALL_NAME = "event_messaged"

do_install() {
        install -d ${D}/var/lib/obmc/events/
        install -m 0755 -d ${D}${sbindir}
        install -m 0755 ${S}/${INSTALL_NAME} ${D}/${sbindir}/obmc-phosphor-eventd
        install -m 0755 -d ${D}${sysconfdir}/default/eventd
        install -m 0644 ${WORKDIR}/eventd.conf ${D}${sysconfdir}/default/eventd/eventd.conf
}
