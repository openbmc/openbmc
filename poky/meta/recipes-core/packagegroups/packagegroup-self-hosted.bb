#
# Copyright (C) 2010 Intel Corporation
#

SUMMARY = "Self-hosting"
DESCRIPTION = "Packages required to run the build system"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup  features_check
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

RDEPENDS:packagegroup-self-hosted = "\
    packagegroup-self-hosted-debug \
    packagegroup-self-hosted-sdk \
    packagegroup-self-hosted-extended \
    packagegroup-self-hosted-graphics \
    packagegroup-self-hosted-host-tools \
    "

RDEPENDS:packagegroup-self-hosted-host-tools = "\
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    hdparm \
    iptables \
    lsb-release \
    mc \
    mc-shell \
    mc-helpers \
    mc-helpers-perl \
    parted \
    ${PSEUDO} \
    screen \
    "
PSEUDO = "pseudo"
PSEUDO:libc-musl = ""

RRECOMMENDS:packagegroup-self-hosted-host-tools = "\
    kernel-module-tun \
    kernel-module-iptable-raw \
    kernel-module-iptable-nat \
    kernel-module-iptable-mangle \
    kernel-module-iptable-filter \
	"

RDEPENDS:packagegroup-self-hosted-sdk = "\
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
RDEPENDS:packagegroup-self-hosted-sdk:append:mingw32 = "\
    libssp \
    libssp-dev \
    libssp-staticdev \
    "
# rpcsvc-proto: for rpcgen
RDEPENDS:packagegroup-self-hosted-sdk:append:libc-glibc = "\
    glibc-gconv-ibm850 \
    glibc-utils \
    rpcsvc-proto \
    "

STRACE = "strace"
STRACE:riscv32 = ""
RDEPENDS:packagegroup-self-hosted-debug = " \
    gdb \
    gdbserver \
    rsync \
    ${STRACE} \
    tcf-agent"


RDEPENDS:packagegroup-self-hosted-extended = "\
    bzip2 \
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
    libaio \
    libusb1 \
    libxml2 \
    lsof \
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
    python3 \
    python3-modules \
    python3-git \
    quota \
    readline \
    rpm \
    setserial \
    settings-daemon \
    socat \
    subversion \
    sudo \
    sysstat \
    tar \
    tcl \
    texinfo \
    unzip \
    usbutils \
    watchdog \
    wget \
    which \
    xinetd \
    xz \
    zip \
    zlib \
    zstd \
    "


RDEPENDS:packagegroup-self-hosted-graphics = "\
    adwaita-icon-theme \
    builder \
    l3afpad \
    libgl \
    libgl-dev \
    libglu \
    libglu-dev \
    libx11-dev \
    pcmanfm \
    vte \
    xdg-utils \
    "
