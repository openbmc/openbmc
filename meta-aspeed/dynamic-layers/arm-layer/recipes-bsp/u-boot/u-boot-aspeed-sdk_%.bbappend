do_compile[depends] += " \
    ${@oe.utils.conditional('UBOOT_FIT_TEE', '1', 'optee-os:do_deploy', '', d)} \
    ${@oe.utils.conditional('UBOOT_FIT_ARM_TRUSTED_FIRMWARE', '1', 'trusted-firmware-a:do_deploy', '', d)} \
    "
