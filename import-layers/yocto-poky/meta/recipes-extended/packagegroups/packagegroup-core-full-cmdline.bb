#
# Copyright (C) 2010 Intel Corporation
#

SUMMARY = "Standard full-featured Linux system"
DESCRIPTION = "Package group bringing in packages needed for a more traditional full-featured Linux system"
PR = "r6"

inherit packagegroup

PACKAGES = "\
    packagegroup-core-full-cmdline \
    packagegroup-core-full-cmdline-libs \
    packagegroup-core-full-cmdline-utils \
    packagegroup-core-full-cmdline-extended \
    packagegroup-core-full-cmdline-dev-utils \
    packagegroup-core-full-cmdline-multiuser \
    packagegroup-core-full-cmdline-initscripts \
    packagegroup-core-full-cmdline-sys-services \
    "

python __anonymous () {
    # For backwards compatibility after rename
    namemap = {}
    namemap["packagegroup-core-full-cmdline"] = "packagegroup-core-basic"
    namemap["packagegroup-core-full-cmdline-libs"] = "packagegroup-core-basic-libs"
    namemap["packagegroup-core-full-cmdline-utils"] = "packagegroup-core-basic-utils"
    namemap["packagegroup-core-full-cmdline-extended"] = "packagegroup-core-basic-extended"
    namemap["packagegroup-core-full-cmdline-dev-utils"] = "packagegroup-core-dev-utils"
    namemap["packagegroup-core-full-cmdline-multiuser"] = "packagegroup-core-multiuser"
    namemap["packagegroup-core-full-cmdline-initscripts"] = "packagegroup-core-initscripts"
    namemap["packagegroup-core-full-cmdline-sys-services"] = "packagegroup-core-sys-services"

    packages = d.getVar("PACKAGES").split()
    for pkg in packages:
        if pkg.endswith('-dev'):
            mapped = namemap.get(pkg[:-4], None)
            if mapped:
                mapped += '-dev'
        elif pkg.endswith('-dbg'):
            mapped = namemap.get(pkg[:-4], None)
            if mapped:
                mapped += '-dbg'
        else:
            mapped = namemap.get(pkg, None)

        if mapped:
            oldtaskname = mapped.replace("packagegroup-core", "task-core")
            mapstr = " %s %s" % (mapped, oldtaskname)
            d.appendVar("RPROVIDES_%s" % pkg, mapstr)
            d.appendVar("RREPLACES_%s" % pkg, mapstr)
            d.appendVar("RCONFLICTS_%s" % pkg, mapstr)
}


RDEPENDS_packagegroup-core-full-cmdline = "\
    packagegroup-core-full-cmdline-libs \
    packagegroup-core-full-cmdline-utils \
    packagegroup-core-full-cmdline-extended \
    packagegroup-core-full-cmdline-dev-utils \
    packagegroup-core-full-cmdline-multiuser \
    packagegroup-core-full-cmdline-initscripts \
    packagegroup-core-full-cmdline-sys-services \
    "

RDEPENDS_packagegroup-core-full-cmdline-libs = "\
    glib-2.0 \
    "

RDEPENDS_packagegroup-core-full-cmdline-utils = "\
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
    gmp \
    grep \
    makedevs \
    mc \
    mc-fish \
    mc-helpers \
    mc-helpers-perl \
    mktemp \
    ncurses \
    net-tools \
    popt \
    procps \
    psmisc \
    sed \
    tar \
    time \
    util-linux \
    zlib \
    "

RDEPENDS_packagegroup-core-full-cmdline-extended = "\
    iproute2 \
    iputils \
    iptables \
    module-init-tools \
    openssl \
    "

RDEPENDS_packagegroup-core-full-cmdline-dev-utils = "\
    diffutils \
    m4 \
    make \
    patch \
    "

VIRTUAL-RUNTIME_initscripts ?= "initscripts"
VIRTUAL-RUNTIME_init_manager ?= "sysvinit"
VIRTUAL-RUNTIME_login_manager ?= "busybox"
VIRTUAL-RUNTIME_syslog ?= "sysklogd"
RDEPENDS_packagegroup-core-full-cmdline-initscripts = "\
    ${VIRTUAL-RUNTIME_initscripts} \
    ${VIRTUAL-RUNTIME_init_manager} \
    ethtool \
    ${VIRTUAL-RUNTIME_login_manager} \
    ${VIRTUAL-RUNTIME_syslog} \
    "

RDEPENDS_packagegroup-core-full-cmdline-multiuser = "\
    cracklib \
    gzip \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libuser', '', d)} \
    shadow \
    sudo \
    "

RDEPENDS_packagegroup-core-full-cmdline-sys-services = "\
    at \
    bzip2 \
    cronie \
    dbus \
    elfutils \
    gzip \
    less \
    logrotate \
    nfs-utils \
    pciutils \
    rpcbind \
    tcp-wrappers \
    tzdata \
    "
