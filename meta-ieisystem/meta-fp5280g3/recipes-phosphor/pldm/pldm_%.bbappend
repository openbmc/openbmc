PACKAGECONFIG = "transport-mctp-demux oem-ibm"

EXTRA_OEMESON += " \
        -Dsoftoff-timeout-seconds=2700 \
        "

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'oem-ibm', \
        'pldm-create-phyp-nvram.service \
        pldm-create-phyp-nvram-cksum.service \
        ', '', d)}"
