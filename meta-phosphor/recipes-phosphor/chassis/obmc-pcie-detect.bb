SUMMARY = "OpenBMC PCIE slot detection utility"
DESCRIPTION = "OpenBMC PCIE slot detection utility."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-systemd
inherit pkgconfig

SKELETON_DIR = "pciedetect"

TMPL = "pcie-slot-detect@.service"
TGTFMT = "obmc-host-startmin@{0}.target"
INSTFMT = "pcie-slot-detect@{0}.service"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "pcie-slot-detect@.service"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
