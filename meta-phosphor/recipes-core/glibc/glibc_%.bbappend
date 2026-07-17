PACKAGECONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'ldap', ' nscd', '', d)}"
