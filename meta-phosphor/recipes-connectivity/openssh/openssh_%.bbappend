# Don't create an empty openssh-dev package. That would pull in openssh-sshd
# even it we are only depending on openssh-sftp, which causes conflicts
# with dropbear
ALLOW_EMPTY:${PN}-dev = "0"
