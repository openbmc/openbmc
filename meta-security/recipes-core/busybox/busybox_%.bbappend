require ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'busybox_libsecomp.inc', '', d)}
