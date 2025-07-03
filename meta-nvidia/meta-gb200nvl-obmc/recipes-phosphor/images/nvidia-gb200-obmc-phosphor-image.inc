OBMC_IMAGE_EXTRA_INSTALL:append = "\
    biosconfig-manager \
    ipmitool \
    mctp \
    platform-init \
    smbios-mdr \
    usb-ethernet-init \
"

OBMC_IMAGE_EXTRA_INSTALL:append:nv-gpu-pcie-card = "\
    gpu-card-init \
"
