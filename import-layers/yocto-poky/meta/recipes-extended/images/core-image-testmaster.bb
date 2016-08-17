DESCRIPTION = "A master image to be deployed on a target useful for testing other images"

IMAGE_FEATURES += "ssh-server-openssh package-management"

inherit core-image

# the deploy code requires bash and
# normal linux utilities not busybox ones
IMAGE_INSTALL += "\
    bash coreutils util-linux tar gzip bzip2 kmod \
    python-modules python-misc \
    e2fsprogs e2fsprogs-mke2fs parted \
    "
# we need a particular initramfs for live images
# that pulls custom install scripts which take
# care of partitioning for us
INITRD_IMAGE = "core-image-testmaster-initramfs"

