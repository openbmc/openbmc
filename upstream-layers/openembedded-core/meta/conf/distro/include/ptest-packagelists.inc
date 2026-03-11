#
# Lists of the ptests in OE-Core, sorted into two sets by the time they take
#
# ptests which take less than ~30s each
#
PTESTS_FAST = "\
    acl \
    apr-util \
    attr \
    babeltrace \
    babeltrace2 \
    bc \
    bluez5 \
    busybox \
    cpio \
    diffstat \
    diffutils \
    ethtool \
    expat \
    expect \
    findutils \
    flex \
    gawk \
    gdbm \
    gdk-pixbuf \
    glib-networking \
    gzip \
    icu \
    json-c \
    json-glib \
    libconvert-asn1-perl \
    libexif \
    libgpg-error\
    libnl \
    libpcre \
    libssh2 \
    libtest-fatal-perl \
    libtest-needs-perl \
    libtest-warnings-perl \
    libtimedate-perl \
    libtry-tiny-perl \
    libusb1 \
    libxml-namespacesupport-perl \
    libxml-parser-perl \
    libxml-perl \
    libxml-sax-base-perl \
    libxml-sax-perl \
    libxml-simple-perl \
    libxml2 \
    libxmlb \
    logrotate \
    lua \
    lzo \
    m4 \
    nettle \
    opkg \
    popt \
    python3-atomicwrites \
    python3-attrs \
    python3-bcrypt \
    python3-calver \
    python3-hypothesis \
    python3-idna \
    python3-jinja2 \
    python3-jsonpointer \
    python3-libarchive-c \
    python3-license-expression \
    python3-mako \
    python3-markupsafe \
    python3-more-itertools \
    python3-packaging \
    python3-pluggy \
    python3-pefile \
    python3-pyasn1 \
    python3-pytz \
    python3-pyyaml \
    python3-rpds-py \
    python3-trove-classifiers \
    python3-uritools \
    python3-wcwidth \
    python3-webcolors \
    python3-wheel \
    qemu \
    quilt \
    rpm-sequoia \
    sed \
    slang \
    wayland \
    xz \
    zlib \
"
PTESTS_FAST:remove:mips64 = "qemu"
PTESTS_PROBLEMS:append:mips64 = " qemu"
PTESTS_FAST:remove:riscv32 = "qemu"
PTESTS_PROBLEMS:append:riscv32 = " qemu"

PTESTS_SLOW = "\
    apr \
    bzip2 \
    coreutils \
    curl \
    dbus \
    dosfstools \
    e2fsprogs \
    elfutils \
    gettext \
    glib-2.0 \
    gnutls \
    gstreamer1.0 \
    less \
    libevent \
    libgcrypt \
    libmodule-build-perl \
    libpng \
    libseccomp \
    lttng-tools \
    lz4 \
    openssh \
    openssl \
    parted \
    perl \
    python3 \
    python3-cffi \
    python3-click \
    python3-cryptography \
    python3-numpy \
    python3-xmltodict \
    strace \
    tar \
    tcl \
    tcl8 \
    util-linux \
"

# python3 ptests hang on qemuriscv64
PTESTS_SLOW:remove:riscv64 = "python3"
PTESTS_PROBLEMS:append:riscv64 = " python3"
PTESTS_SLOW:remove:riscv32 = "lttng-tools strace "
PTESTS_PROBLEMS:append:riscv32 = " lttng-tools strace"
PTESTS_SLOW:append:libc-musl = " libc-test"

# These tests don't yet pass for riscv64
PTESTS_SLOW:remove:riscv64 = "tcl tcl8 python3-cffi strace lttng-tools python3-numpy perl"
PTESTS_PROBLEMS:append:riscv64 = " tcl tcl8 python3-cffi strace lttng-tools python3-numpy perl"

#    ruby \ # Timeout
#    rt-tests \ # Needs to be checked whether it runs at all
#    bash \ # Test outcomes are non-deterministic by design
#    ifupdown \ # Tested separately in lib/oeqa/selftest/cases/imagefeatures.py
#    libinput \ # Tests need an unloaded system to be reliable
#    libpam \ # Needs pam DISTRO_FEATURE
#    mdadm \ # tests are flaky in AB.
#    numactl \ # qemu not (yet) configured for numa; all tests are skipped

PTESTS_PROBLEMS = "\
    ruby \
    rt-tests \
    bash \
    ifupdown \
    libinput \
    libpam \
    mdadm \
    numactl \
    python3-license-expression \
"
