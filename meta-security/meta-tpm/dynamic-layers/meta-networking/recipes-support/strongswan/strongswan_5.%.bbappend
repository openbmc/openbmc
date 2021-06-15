require ${@bb.utils.contains('DISTRO_FEATURES', 'tpm', 'strongswan-tpm.inc', '', d)}
