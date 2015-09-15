DESCRIPTION = "Sample image recipe using Phosphor, an OpenBMC \
framework, illustrating the removal of a default image feature."

require obmc-phosphor-image.bb

IMAGE_FEATURES_remove = " \
        obmc-phosphor-system-mgmt \
        "
