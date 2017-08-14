SUMMARY = "Witherspoon Power Fault Analysis"
DESCRIPTION = "Analyzes power devices for faults"
PR = "r1"

inherit autotools
inherit pkgconfig
inherit obmc-phosphor-systemd
inherit pythonnative

require ${PN}.inc

S = "${WORKDIR}/git"

DEPENDS += " \
         phosphor-logging \
         autoconf-archive-native \
         sdbus++-native \
         "

RDEPENDS_${PN} += " \
         phosphor-logging \
         phosphor-dbus-interfaces \
         sdbusplus \
         "

CHASSIS_ON_TGT = "obmc-chassis-poweron@0.target"
SEQ_PGOOD_SVC = "witherspoon-pseq-monitor-pgood.service"
SEQ_PGOOD_FMT = "../${SEQ_PGOOD_SVC}:${CHASSIS_ON_TGT}.wants/${SEQ_PGOOD_SVC}"

SYSTEMD_SERVICE_${PN} += "${SEQ_PGOOD_SVC}"
SYSTEMD_LINK_${PN} += "${SEQ_PGOOD_FMT}"
