do_install_append_harden () {
	# to hardend
	sed -i -e 's:#AllowTcpForwarding yes:AllowTcpForwarding no:' ${D}${sysconfdir}/ssh/sshd_config
	sed -i -e 's:ClientAliveCountMax 4:ClientAliveCountMax 2:' ${D}${sysconfdir}/ssh/sshd_config
	sed -i -e 's:#LogLevel INFO:LogLevel VERBOSE:' ${D}${sysconfdir}/ssh/sshd_config
	sed -i -e 's:#MaxSessions.*:MaxSessions 2:' ${D}${sysconfdir}/ssh/sshd_config
	sed -i -e 's:#TCPKeepAlive yes:TCPKeepAlive no:' ${D}${sysconfdir}/ssh/sshd_config
	sed -i -e 's:#AllowAgentForwarding yes:AllowAgentForwarding no:' ${D}${sysconfdir}/ssh/sshd_config

    if [ "${@bb.utils.contains('DISABLE_ROOT', 'True', 'yes', 'no', d)}" = "yes" ]; then
        sed -i -e 's:#PermitRootLogin.*:PermitRootLogin prohibit-password:' ${D}${sysconfdir}/ssh/sshd_config
    fi
}
