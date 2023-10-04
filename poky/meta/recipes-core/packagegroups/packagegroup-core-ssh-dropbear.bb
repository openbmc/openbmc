SUMMARY = "Dropbear SSH client/server"

inherit packagegroup

RDEPENDS:${PN} = "dropbear"
RRECOMMENDS:${PN} = "openssh-sftp-server"
