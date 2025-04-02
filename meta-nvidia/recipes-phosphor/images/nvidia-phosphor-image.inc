IMAGE_FEATURES:remove = "\
    obmc-net-ipmi \
    obmc-user-mgmt-ldap \
    ssh-server-dropbear \
"

IMAGE_FEATURES:append = "\
    ssh-server-openssh \
"

OBMC_IMAGE_EXTRA_INSTALL:append = "\
    curl \
    dbus-top \
    ethtool \
    iperf3 \
    iproute2 \
    jq \
    mmc-utils \
    pciutils \
    phytool \
    strace \
    systemd-analyze \
    tcpdump \
    tmux \
    usbutils \
    wget \
"

IMAGE_LINGUAS="en-us"

include recipes-phosphor/images/nvidia-${MACHINE}-phosphor-image.inc
