# Force the mctp-demux to be used until machine is ready to use in-kernel MCTP
PACKAGECONFIG = "transport-mctp-demux"

PACKAGECONFIG += "oem-ibm"
PACKAGECONFIG[oem-ibm] = "-Doem-ibm=enabled, -Doem-ibm=disabled, , squashfs-tools"

EXTRA_OEMESON += " \
        -Dsoftoff-timeout-seconds=2700 \
        "

#5 second timeout defined inside PLDM has seen issues during reset reload
#so increasing that to 10 seconds here.IBMs custom firmware stack can tolerate
#PLDM timeouts of up to 20 seconds, so using timeout value of 10 seconds is safe.
EXTRA_OEMESON += " \
         -Ddbus-timeout-value=10 \
        "

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'oem-ibm', \
    'pldm-create-phyp-nvram.service \
     pldm-create-phyp-nvram-cksum.service \
    ', '', d)}"
