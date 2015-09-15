SUMMARY = "Remote debugging tools for Eclipse integration"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS_${PN} = "\
    gdbserver \
    tcf-agent \
    openssh-sftp-server \
    "
