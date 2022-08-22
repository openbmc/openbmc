SUMMARY = "Dropbear SSH client/server"
PR = "r1"

inherit packagegroup

RDEPENDS_${PN} = "dropbear"
RRECOMMENDS_${PN} = "openssh-sftp-server"
