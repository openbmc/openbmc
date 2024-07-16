IMAGE_FEATURES:remove = "\
    obmc-net-ipmi \
    obmc-user-mgmt-ldap \
    ssh-server-dropbear \
    "
IMAGE_FEATURES:append = "\
    ssh-server-openssh \
    "
IMAGE_FEATURES:remove:fb-nohost = "\
    obmc-console \
    "

OBMC_IMAGE_EXTRA_INSTALL:append = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'tpm', \
        bb.utils.contains('MACHINE_FEATURES', 'tpm2', 'packagegroup-security-tpm2 libtss2-tcti-device', '', d), \
        '', d)} \
    curl \
    dbus-top \
    iproute2 \
    jq \
    strace \
    tcpdump \
    tmux \
    usbutils \
    wget \
    "
OBMC_IMAGE_EXTRA_INSTALL:append:mf-fb-secondary-emmc = " \
    emmc-init \
    mmc-utils \
    util-linux-blkdiscard \
    "
OBMC_IMAGE_EXTRA_INSTALL:append:mf-fb-ncsi-nic = " \
    network-wait-ipv6-ll \
    "

IMAGE_LINGUAS="en-us"

include recipes-phosphor/images/fb-${MACHINE}-phosphor-image.inc
