require ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', '${BPN}-meta-virtualization.inc', '', d)}
