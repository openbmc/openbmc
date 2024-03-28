FILESEXTRAPATHS:prepend := "${THISDIR}:"
SRC_URI:append = " \
        file://systemd-bootchart.cfg \
        "
SRC_URI:append:df-mctp = " \
        file://mctp/mctp.scc \
        "
SRC_URI:append:df-nfs = " \
        file://nfs/nfs.scc \
        file://nfs/nfs.cfg \
        "
