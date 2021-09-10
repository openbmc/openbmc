SUMMARY = "Remote debugging tools for Eclipse integration"

inherit packagegroup

RDEPENDS:${PN} = "\
    gdbserver \
    tcf-agent \
    openssh-sftp-server \
    "
