# This class is to be inherited by recipes interested in only deploying what is
# listed in the EXTRA_IMAGEDEPENDS.
# It is inheriting the image.bbclass to make sure that the
# image_license.manifest is generated.

IMAGE_FSTYPES = ""

inherit image

IMAGE_FEATURES = ""
EXTRA_IMAGE_FEATURES = ""

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = ""
RDEPENDS = ""
RRECOMMENDS = ""

deltask do_prepare_recipe_sysroot
deltask do_flush_pseudodb
deltask do_image_qa
do_rootfs[depends] = ""
do_rootfs[noexec] = "1"
do_image[noexec] = "1"
do_image_complete[noexec] = "1"
do_build[depends] = ""

IMAGE_CLASSES:remove = "create-spdx-image-3.0"
