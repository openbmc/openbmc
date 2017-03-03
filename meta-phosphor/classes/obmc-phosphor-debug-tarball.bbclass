inherit image
inherit obmc-phosphor-license

IMAGE_FSTYPES = "tar.xz"
IMAGE_INSTALL_append = " busybox packagegroup-obmc-phosphor-debugtools"

# Remove packages installed by 'extrausers'.
IMAGE_INSTALL_remove = "base-passwd shadow"
EXTRA_USERS_PARAMS = ""

# Remove extra packages defaulted by image.bbclass.
PACKAGE_INSTALL = "${IMAGE_INSTALL}"
IMAGE_LINGUAS = ""
