SUMMARY = "Dropbear SSH client/server"
PR = "r1"

inherit packagegroup

RDEPENDS:${PN} = "dropbear"
RRECOMMENDS:${PN} = "openssh-sftp-server"
