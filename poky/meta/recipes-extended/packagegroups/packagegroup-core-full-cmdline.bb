#
# Copyright (C) 2010 Intel Corporation
#

SUMMARY = "Standard full-featured Linux system"
DESCRIPTION = "Package group bringing in packages needed for a more traditional full-featured Linux system"

inherit packagegroup

PACKAGES = "\
    packagegroup-core-full-cmdline \
    packagegroup-core-full-cmdline-utils \
    packagegroup-core-full-cmdline-extended \
    packagegroup-core-full-cmdline-dev-utils \
    packagegroup-core-full-cmdline-multiuser \
    packagegroup-core-full-cmdline-initscripts \
    packagegroup-core-full-cmdline-sys-services \
    "

RDEPENDS:packagegroup-core-full-cmdline = "\
    packagegroup-core-full-cmdline-utils \
    packagegroup-core-full-cmdline-extended \
    packagegroup-core-full-cmdline-dev-utils \
    packagegroup-core-full-cmdline-multiuser \
    packagegroup-core-full-cmdline-initscripts \
    packagegroup-core-full-cmdline-sys-services \
    "

RDEPENDS:packagegroup-core-full-cmdline-utils = "\
    bash \
    acl \
    attr \
    bc \
    coreutils \
    cpio \
    e2fsprogs \
    ed \
    file \
    findutils \
    gawk \
    grep \
    less \
    makedevs \
    mc \
    mc-shell \
    mc-helpers \
    mc-helpers-perl \
    ncurses \
    net-tools \
    procps \
    psmisc \
    sed \
    tar \
    time \
    util-linux \
    "

RDEPENDS:packagegroup-core-full-cmdline-extended = "\
    iproute2 \
    iputils \
    iptables \
    module-init-tools \
    openssl \
    "

RDEPENDS:packagegroup-core-full-cmdline-dev-utils = "\
    diffutils \
    m4 \
    make \
    patch \
    "

VIRTUAL-RUNTIME_syslog ?= "sysklogd"
RDEPENDS:packagegroup-core-full-cmdline-initscripts = "\
    ${VIRTUAL-RUNTIME_initscripts} \
    ${VIRTUAL-RUNTIME_init_manager} \
    ethtool \
    ${VIRTUAL-RUNTIME_login_manager} \
    ${VIRTUAL-RUNTIME_syslog} \
    "

RDEPENDS:packagegroup-core-full-cmdline-multiuser = "\
    bzip2 \
    cracklib \
    gzip \
    shadow \
    sudo \
    "

RDEPENDS:packagegroup-core-full-cmdline-sys-services = "\
    at \
    cronie \
    logrotate \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nfs', 'nfs-utils rpcbind', '', d)} \
    "
