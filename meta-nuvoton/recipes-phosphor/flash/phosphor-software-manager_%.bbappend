require ${@bb.utils.contains('DISTRO_FEATURES', 'phosphor-mmc', 'rm-mirror-uboot.inc', '', d)}
