FILESEXTRAPATHS:prepend := "${THISDIR}:"
SRC_URI:append = " \
        file://systemd-bootchart.cfg \
        ${@bb.utils.contains('DISTRO_FEATURES', 'mctp', 'file://mctp/mctp.scc file://mctp/mctp.cfg', '', d)} \
        "
