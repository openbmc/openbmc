require recipes-core/images/core-image-minimal.bb

# The core-image-minimal is used for the initramfs bundle for the
# Corstone1000 but the testimage task caused hanging errors. This is
# why the core-image-minimal is forked here so the testimage task can
# be disabled as it is not relevant for the Corstone1000.
IMAGE_CLASSES:remove = "testimage"
