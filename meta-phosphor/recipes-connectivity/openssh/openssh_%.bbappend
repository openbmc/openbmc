# Don't create an empty openssh-dev package. That would pull in openssh-sshd
# even it we are only depending on openssh-sftp, which causes conflicts
# with dropbear
ALLOW_EMPTY:${PN}-dev = "0"
do_install:append() {
    CONF_FILE="${D}${sysconfdir}/ssh/sshd_config"

    if [ -f "$CONF_FILE" ]; then
        {
            echo ""
            echo "#Exclude weak MACs (SHA1, UMAC-64)"
            echo "MACs -hmac-sha1*,umac-64*"
        } >> "$CONF_FILE"
    fi
}
