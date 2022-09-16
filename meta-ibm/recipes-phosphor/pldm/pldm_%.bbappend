PACKAGECONFIG += "oem-ibm"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled, -Doem-ibm=disabled, , squashfs-tools"

EXTRA_OEMESON += " \
        -Dsoftoff-timeout-seconds=2700 \
        "

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'oem-ibm', \
    'pldm-create-phyp-nvram.service \
     pldm-create-phyp-nvram-cksum.service \
    ', '', d)}"
