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
    json-c \
    json-glib \
    libconvert-asn1-perl \
    libgpg-error\
    libnl \
    libpcre \
    libssh2 \
    libtimedate-perl \
    libtest-fatal-perl \
    libtest-needs-perl \
    libtest-warnings-perl \
    libtry-tiny-perl \
    liburi-perl \
    libusb1 \
    libxml-namespacesupport-perl \
    libxml-perl \
    libxml-parser-perl \
    libxml-sax-perl \
    libxml-sax-base-perl \
    libxml-simple-perl \
    libxml2 \
    libxmlb \
    logrotate \
    lua \
    lzo \
    m4 \
    nettle \
    opkg \
    pango \
    popt \
    python3-atomicwrites \
    python3-attrs \
    python3-bcrypt \
    python3-calver \
    python3-hypothesis \
    python3-jinja2 \
    python3-jsonpointer \
    python3-license-expression \
    python3-markupsafe \
    python3-more-itertools \
    python3-pluggy \
    python3-pyasn1 \
    python3-pytz \
    python3-pyyaml \
    python3-trove-classifiers \
    python3-wcwidth \
    python3-webcolors \
    qemu \
    quilt \
    sed \
    slang \
    wayland \
    xz \
    zlib \
    libexif \
"
PTESTS_FAST:append:libc-glibc = " glibc-y2038-tests"
PTESTS_PROBLEMS:remove:libc-glibc = "glibc-y2038-tests"
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
    lttng-tools \
    openssh \
    openssl \
    parted \
    perl \
    python3-cryptography \
    python3 \
    python3-click \
    python3-xmltodict \
    strace \
    tar \
    tcl \
    util-linux \
    valgrind \
    lz4 \
    libseccomp \
"

# python3 ptests hang on qemuriscv64
PTESTS_SLOW:remove:riscv64 = "valgrind python3"
PTESTS_PROBLEMS:append:riscv64 = " valgrind python3"
PTESTS_SLOW:remove:riscv32 = "lttng-tools strace valgrind"
PTESTS_PROBLEMS:append:riscv32 = " lttng-tools strace valgrind"
PTESTS_SLOW:append:libc-musl = " libc-test"
PTESTS_SLOW:remove:x86 = "valgrind"
PTESTS_PROBLEMS:append:x86 = " valgrind"

#    ruby \ # Timeout
#    rt-tests \ # Needs to be checked whether it runs at all
#    bash \ # Test outcomes are non-deterministic by design
#    ifupdown \ # Tested separately in lib/oeqa/selftest/cases/imagefeatures.py
#    libinput \ # Tests need an unloaded system to be reliable
#    libpam \ # Needs pam DISTRO_FEATURE
#    mdadm \ # tests are flaky in AB.
#    numactl \ # qemu not (yet) configured for numa; all tests are skipped
#    python3-numpy \ # requires even more RAM and (possibly) disk space; multiple failures

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
    python3-numpy \
"
