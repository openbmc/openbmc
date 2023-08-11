SUMMARY = "A test controller image to be deployed on a target useful for testing other images using the OEQA runtime tests"

IMAGE_FEATURES += "ssh-server-openssh package-management"

inherit core-image

# the deploy code requires bash and
# normal linux utilities not busybox ones
IMAGE_INSTALL += "\
    bash coreutils util-linux tar gzip bzip2 kmod \
    python3-modules python3-misc \
    e2fsprogs e2fsprogs-mke2fs parted \
    "
# we need a particular initramfs for live images
# that pulls custom install scripts which take
# care of partitioning for us
INITRD_IMAGE = "core-image-testcontroller-initramfs"

