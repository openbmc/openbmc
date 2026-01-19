# Add libubootenv-bin into the initramfs only when the u-boot-fw-utils provider is libubootenv
RDEPENDS:${PN}:append = " ${@bb.utils.contains('PREFERRED_PROVIDER_u-boot-fw-utils', 'libubootenv', 'libubootenv-bin', '', d)}"
RDEPENDS:${PN}:append = " axiado-eip-firmware"
