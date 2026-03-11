require ${@bb.utils.contains('MACHINE_FEATURES', 'uefi-secureboot', 'systemd-boot-uefi-secureboot.inc', '', d)}
