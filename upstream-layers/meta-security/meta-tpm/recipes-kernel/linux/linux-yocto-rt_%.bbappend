require ${@bb.utils.contains_any('DISTRO_FEATURES', 'tpm tpm2', 'linux-yocto_tpm.inc', '', d)}
