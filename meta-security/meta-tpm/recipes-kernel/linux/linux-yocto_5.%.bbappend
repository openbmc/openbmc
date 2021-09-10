require ${@bb.utils.contains_any('DISTRO_FEATURES', 'tpm', 'linux-yocto_tpm.inc', '', d)}
