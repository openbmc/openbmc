# Enable RPMB FS for fTPM
EXTRA_OEMAKE:append = " \
    CFG_REE_FS=n \
    CFG_RPMB_FS=y \
    "
