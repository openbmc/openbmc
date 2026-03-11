require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'base-files-ima.inc', '', d)}
