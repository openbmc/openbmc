PACKAGECONFIG += "oem-ibm"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled, -Doem-ibm=disabled, , squashfs-tools"

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'oem-ibm', \
    'pldm-create-phyp-nvram.service \
     pldm-create-phyp-nvram-cksum.service \
     pldm-reset-phyp-nvram.service \
     pldm-reset-phyp-nvram-cksum.service \
    ', '', d)}"
