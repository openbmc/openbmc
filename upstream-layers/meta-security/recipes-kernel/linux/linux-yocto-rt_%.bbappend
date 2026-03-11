require ${@bb.utils.contains('DISTRO_FEATURES', 'security', 'linux-yocto_security.inc', '', d)}
