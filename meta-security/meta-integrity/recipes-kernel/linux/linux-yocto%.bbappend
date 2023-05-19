require ${@bb.utils.contains_any('DISTRO_FEATURES', 'integrity ', 'linux_ima.inc', '', d)}
