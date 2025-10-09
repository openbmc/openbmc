# Add TPM2 support into initramfs if the machine has TPM2 feature
RDEPENDS:${PN}:append:huygens = " ${@bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'packagegroup-openbmc-tpm', '', d)}"

