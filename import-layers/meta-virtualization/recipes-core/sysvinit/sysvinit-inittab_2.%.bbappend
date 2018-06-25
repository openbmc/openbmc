require ${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'sysvinit-inittab_xen.inc', '', d)}
