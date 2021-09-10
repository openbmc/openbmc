# Platforms with Hoth don't use the U-Boot environment since this would allow
# bypassing attestation.
PACKAGECONFIG:remove:hoth = "uboot-env"
