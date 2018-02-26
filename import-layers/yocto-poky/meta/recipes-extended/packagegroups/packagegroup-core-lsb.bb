#
# Copyright (C) 2010 Intel Corporation
#

SUMMARY = "Linux Standard Base (LSB)"
DESCRIPTION = "Packages required to satisfy the Linux Standard Base (LSB) specification"
PR = "r10"

inherit packagegroup distro_features_check

# The libxt, libxtst and others require x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

# libglu needs virtual/libgl, which requires opengl in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES += "opengl"

#
# We will skip parsing this packagegeoup for non-glibc systems
#
python __anonymous () {
    if d.getVar('TCLIBC') != "glibc":
        raise bb.parse.SkipPackage("incompatible with %s C library" %
                                   d.getVar('TCLIBC'))
}

PACKAGES = "\
    packagegroup-core-lsb \
    packagegroup-core-sys-extended \
    packagegroup-core-db \
    packagegroup-core-perl \
    packagegroup-core-python \
    packagegroup-core-tcl \
    packagegroup-core-lsb-misc \
    packagegroup-core-lsb-core \
    packagegroup-core-lsb-perl \
    packagegroup-core-lsb-python \
    packagegroup-core-lsb-desktop \
    packagegroup-core-lsb-runtime-add \
    "


RDEPENDS_packagegroup-core-lsb = "\
    packagegroup-core-sys-extended \
    packagegroup-core-db \
    packagegroup-core-perl \
    packagegroup-core-python \
    packagegroup-core-tcl \
    packagegroup-core-lsb-misc \
    packagegroup-core-lsb-core \
    packagegroup-core-lsb-perl \
    packagegroup-core-lsb-python \
    packagegroup-core-lsb-desktop \
    packagegroup-core-lsb-runtime-add \
    "


RDEPENDS_packagegroup-core-sys-extended = "\
    curl \
    dhcp-client \
    hdparm \
    lighttpd \
    libaio \
    lrzsz \
    lzo \
    mc \
    mc-fish \
    mc-helpers \
    mc-helpers-perl \
    mdadm \
    minicom \
    neon \
    parted \
    quota \
    screen \
    setserial \
    sysstat \
    udev-extraconf \
    unzip \
    watchdog \
    wget \
    which \
    xinetd \
    zip \
    "

RDEPENDS_packagegroup-core-db = "\
    db \
    sqlite3 \
    "

RDEPENDS_packagegroup-core-perl = "\
    gdbm \
    perl \
    zlib \
    "


RDEPENDS_packagegroup-core-python = "\
    expat \
    gdbm \
    gmp \
    ncurses \
    openssl \
    python \
    readline \
    zip \
    "

RDEPENDS_packagegroup-core-tcl = "\
    tcl \
    "

# Miscellaneous packages required by LSB (or LSB tests)
RDEPENDS_packagegroup-core-lsb-misc = "\
    chkconfig \
    gettext \
    gettext-runtime \
    groff \
    lsbinitscripts \
    lsbtest \
    lsof \
    strace \
    libusb1 \
    usbutils \
    rpm \
    "

SUMMARY_packagegroup-core-lsb-core = "LSB Core"
DESCRIPTION_packagegroup-core-lsb-core = "Packages required to support commands/libraries \
    specified in the LSB Core specification"
RDEPENDS_packagegroup-core-lsb-core = "\
    at \
    bash \
    bc \
    binutils \
    binutils-symlinks \
    coreutils \
    cpio \
    cronie \
    cups \
    diffutils \
    ed \
    glibc-utils \
    elfutils \
    file \
    findutils \
    fontconfig-utils \
    foomatic-filters \
    gawk \
    ghostscript \
    grep \
    gzip \
    localedef \
    lsb \
    m4 \
    make \
    man \
    man-pages \
    mktemp \
    msmtp \
    patch \
    pax \
    procps \
    psmisc \
    sed \
    shadow \
    tar \
    time \
    util-linux \
    xdg-utils \
    \
    glibc \
    libgcc \
    libpam \
    libxml2 \
    ncurses \
    zlib \
    nspr \
    nss \
"

SUMMARY_packagegroup-core-lsb-perl = "LSB Runtime Languages (Perl)"
DESCRIPTION_packagegroup-core-lsb-perl = "Packages required to support libraries \
    specified in the LSB Runtime languages specification (Perl parts)"
RDEPENDS_packagegroup-core-lsb-perl = "\
    perl \
    perl-modules \
    perl-misc \
    perl-pod \
    perl-dev \
    perl-doc \
"

SUMMARY_packagegroup-core-lsb-python = "LSB Runtime Languages (Python)"
DESCRIPTION_packagegroup-core-lsb-python = "Packages required to support libraries \
    specified in the LSB Runtime languages specification (Python parts)"
RDEPENDS_packagegroup-core-lsb-python = "\
    python \
    python-modules \
    python-misc \
"

SUMMARY_packagegroup-core-lsb-desktop = "LSB Desktop"
DESCRIPTION_packagegroup-core-lsb-desktop = "Packages required to support libraries \
    specified in the LSB Desktop specification"
RDEPENDS_packagegroup-core-lsb-desktop = "\
    libxt \
    libxxf86vm \
    libdrm \
    libglu \
    libxi \
    libxtst \
    libx11-locale \
    xorg-minimal-fonts \
    gdk-pixbuf-loader-ico \
    gdk-pixbuf-loader-bmp \
    gdk-pixbuf-loader-ani \
    gdk-pixbuf-xlib \
    liberation-fonts \
    gtk+ \
    atk \
    libasound \
"

RDEPENDS_packagegroup-core-lsb-runtime-add = "\
    ldd \
    pam-plugin-wheel \
    e2fsprogs-mke2fs \
    mkfontdir \
    liburi-perl \
    libxml-parser-perl \
    libxml-perl \
    libxml-sax-perl \
    glibc-localedatas \
    glibc-gconvs \
    glibc-charmaps \
    glibc-binaries \
    glibc-localedata-posix \
    glibc-extra-nss \
    glibc-pcprofile \
"
