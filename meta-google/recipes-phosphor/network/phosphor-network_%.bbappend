# Platforms with Hoth don't use the U-Boot environment since this would allow
# bypassing attestation.
PACKAGECONFIG_remove_hoth = "uboot-env"
