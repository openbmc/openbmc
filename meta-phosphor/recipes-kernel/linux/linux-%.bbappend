FILESEXTRAPATHS:prepend := "${THISDIR}:"
SRC_URI:append = " \
        file://systemd-bootchart.cfg \
        "
SRC_URI:append:df-cgroup = " \
        file://cgroup/cgroup.scc \
        "
SRC_URI:append:df-mctp = " \
        file://mctp/mctp.scc \
        "
SRC_URI:append:df-nfs = " \
        file://nfs/nfs.scc \
        file://nfs/nfs.cfg \
        "
SRC_URI:append:df-phosphor-production = " \
        file://production/hung-task.scc \
        file://production/hung-task.cfg \
        "
