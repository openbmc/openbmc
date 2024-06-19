SUMMARY = "Corstone1000 platform esp Image"
DESCRIPTION = "This builds a simple image file that only contains an esp \
               partition for use when running the SystemReady IR ACS tests."
LICENSE = "MIT"

COMPATIBLE_MACHINE = "corstone1000"

# IMAGE_FSTYPES must be set before 'inherit image'
# https://docs.yoctoproject.org/ref-manual/variables.html#term-IMAGE_FSTYPES
IMAGE_FSTYPES = "wic"

inherit image

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

PACKAGE_INSTALL = ""

# This builds a very specific image so we can ignore any customization
WKS_FILE = "efi-disk-esp-only.wks.in"
WKS_FILE:firmware = "efi-disk-esp-only.wks.in"

EXTRA_IMAGEDEPENDS = ""
# Don't write an fvp configuration file for this image as it can't run
IMAGE_POSTPROCESS_COMMAND:remove = "do_write_fvpboot_conf;"
