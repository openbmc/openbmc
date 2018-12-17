#
# Copyright (C) 2010 Intel Corporation
#

SUMMARY = "Self-hosting"
DESCRIPTION = "Packages required to run the build system"
PR = "r13"

inherit packagegroup  distro_features_check
# rdepends on libx11-dev
REQUIRED_DISTRO_FEATURES = "x11"

# rdepends on libgl
REQUIRED_DISTRO_FEATURES += "opengl"

PACKAGES = "\
    packagegroup-self-hosted \
    packagegroup-self-hosted-debug \
    packagegroup-self-hosted-sdk \
    packagegroup-self-hosted-extended \
    packagegroup-self-hosted-graphics \
    packagegroup-self-hosted-host-tools \
    "

RDEPENDS_packagegroup-self-hosted = "\
    packagegroup-self-hosted-debug \
    packagegroup-self-hosted-sdk \
    packagegroup-self-hosted-extended \
    packagegroup-self-hosted-graphics \
    packagegroup-self-hosted-host-tools \
    "

RDEPENDS_packagegroup-self-hosted-host-tools = "\
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    hdparm \
    iptables \
    lsb \
    mc \
    mc-fish \
    mc-helpers \
    mc-helpers-perl \
    parted \
    ${PSEUDO} \
    screen \
    "
PSEUDO = "pseudo"
PSEUDO_libc-musl = ""

RRECOMMENDS_packagegroup-self-hosted-host-tools = "\
    kernel-module-tun \
    kernel-module-iptable-raw \
    kernel-module-iptable-nat \
    kernel-module-iptable-mangle \
    kernel-module-iptable-filter \
	"

RDEPENDS_packagegroup-self-hosted-sdk = "\
    autoconf \
    automake \
    binutils \
    binutils-symlinks \
    ccache \
    coreutils \
    cpp \
    cpp-symlinks \
    distcc \
    file \
    findutils \
    g++ \
    g++-symlinks \
    gcc \
    gcc-symlinks \
    intltool \
    ldd \
    less \
    libstdc++ \
    libstdc++-dev \
    libtool \
    make \
    perl-module-re \
    perl-module-text-wrap \
    pkgconfig \
    quilt \
    sed \
    "
RDEPENDS_packagegroup-self-hosted-sdk_append_mingw32 = "\
    libssp \
    libssp-dev \
    libssp-staticdev \
    "
# rpcsvc-proto: for rpcgen
RDEPENDS_packagegroup-self-hosted-sdk_append_libc-glibc = "\
    glibc-gconv-ibm850 \
    glibc-utils \
    rpcsvc-proto \
    "
RDEPENDS_packagegroup-self-hosted-debug = " \
    gdb \
    gdbserver \
    rsync \
    strace \
    tcf-agent"


RDEPENDS_packagegroup-self-hosted-extended = "\
    bzip2 \
    chkconfig \
    chrpath \
    cpio \
    curl \
    diffstat \
    diffutils \
    elfutils \
    expat \
    gawk \
    gdbm \
    gettext \
    gettext-runtime \
    git \
    git-perltools \
    grep \
    groff \
    gzip \
    settings-daemon \
    libaio \
    libusb1 \
    libxml2 \
    lrzsz \
    lsof \
    lzo \
    man \
    man-pages \
    mdadm \
    minicom \
    mtools \
    ncurses \
    ncurses-terminfo-base \
    nfs-utils \
    nfs-utils-client \
    openssl \
    openssh-scp \
    openssh-sftp-server \
    openssh-ssh \
    opkg \
    opkg-utils \
    patch \
    perl \
    perl-dev \
    perl-misc \
    perl-modules \
    perl-pod \
    python \
    python-modules \
    python3 \
    python3-modules \
    python3-git \
    quota \
    readline \
    rpm \
    setserial \
    socat \
    subversion \
    sudo \
    sysstat \
    tar \
    tcl \
    texi2html \
    texinfo \
    unzip \
    usbutils \
    watchdog \
    wget \
    which \
    xinetd \
    zip \
    zlib \
    xz \
    "


RDEPENDS_packagegroup-self-hosted-graphics = "\
    builder \
    libgl \
    libgl-dev \
    libglu \
    libglu-dev \
    libx11-dev \
    adwaita-icon-theme \
    xdg-utils \
    epiphany \
    l3afpad \
    pcmanfm \
    vte \
    "
