PACKAGECONFIG += "oem-ibm"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled, -Doem-ibm=disabled, , squashfs-tools"

SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'oem-ibm', 'pldm-create-phyp-nvram.service pldm-create-phyp-nvram-cksum.service', '', d)}"
