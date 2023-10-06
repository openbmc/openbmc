FILESEXTRAPATHS:prepend := "${THISDIR}:"
SRC_URI:append = " \
        file://systemd-bootchart.cfg \
        "
SRC_URI:append:df-mctp = " \
        file://mctp/mctp.scc \
        file://mctp/mctp.cfg \
        "
