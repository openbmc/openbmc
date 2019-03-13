DESCRIPTION = "Image with Phosphor, an OpenBMC framework."

IMAGE_LINGUAS = ""

inherit obmc-phosphor-image

OBMC_IMAGE_EXTRA_INSTALL_append = "\
    phosphor-bmcweb-cert-config \
    "
