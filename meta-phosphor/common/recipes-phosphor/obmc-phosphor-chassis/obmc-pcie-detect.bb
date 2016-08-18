SUMMARY = "OpenBMC PCIE slot detection utility"
DESCRIPTION = "OpenBMC PCIE slot detection utility."
PR = "r1"

inherit skeleton-gdbus
inherit obmc-phosphor-systemd

SKELETON_DIR = "pciedetect"
SYSTEMD_SERVICE_${PN} += "pcie-slot-detect@.service"
SYSTEMD_GENLINKS_${PN} += "../pcie-slot-detect@.service:obmc-chassis-start@[0].target.wants/pcie-slot-detect@[0].service:OBMC_CHASSIS_INSTANCES"
