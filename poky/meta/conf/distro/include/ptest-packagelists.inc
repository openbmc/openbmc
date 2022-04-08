#
# Lists of the ptests in OE-Core, sorted into two sets by the time they take
#
# ptests which take less than ~30s each
#
PTESTS_FAST = "\
    acl-ptest \
    apr-ptest \
    apr-util-ptest \
    attr-ptest \
    bluez5-ptest \
    bzip2-ptest \
    diffstat-ptest \
    diffutils-ptest \
    elfutils-ptest \
    ethtool-ptest \
    expat-ptest \
    flex-ptest \
    gawk-ptest \
    gdbm-ptest \
    gdk-pixbuf-ptest \
    gettext-ptest \
    glib-networking-ptest \
    gzip-ptest \
    json-glib-ptest \
    libconvert-asn1-perl-ptest \
    liberror-perl-ptest \
    libnl-ptest \
    libmodule-build-perl-ptest \
    libpcre-ptest \
    libssh2-ptest \
    libtimedate-perl-ptest \
    libtest-needs-perl-ptest \
    liburi-perl-ptest \
    libusb1-ptest \
    libxml-namespacesupport-perl-ptest \
    libxml-perl-ptest \
    libxml-parser-perl-ptest \
    libxml-sax-perl-ptest \
    libxml-sax-base-perl-ptest \
    libxml-simple-perl-ptest \
    libxml2-ptest \
    lua-ptest \
    lzo-ptest \
    m4-ptest \
    nettle-ptest \
    openssl-ptest \
    opkg-ptest \
    pango-ptest \
    parted-ptest \
    popt-ptest \
    python3-atomicwrites-ptest \
    python3-bcrypt-ptest \
    python3-hypothesis-ptest \
    python3-jinja2-ptest \
    python3-jsonpointer-ptest \
    python3-markupsafe-ptest \
    python3-more-itertools-ptest \
    python3-pluggy-ptest \
    python3-pyasn1-ptest \
    python3-pytz-ptest \
    python3-wcwidth-ptest \
    python3-webcolors-ptest \
    qemu-ptest \
    quilt-ptest \
    sed-ptest \
    slang-ptest \
    wayland-ptest \
    zlib-ptest \
"
PTESTS_FAST:append:libc-glibc = " glibc-tests-ptest"
PTESTS_PROBLEMS:remove:libc-glibc = "glibc-tests-ptest"
PTESTS_FAST:remove:mips64 = "qemu-ptest"
PTESTS_PROBLEMS:append:mips64 = " qemu-ptest"
PTESTS_FAST:remove:riscv32 = "qemu-ptest"
PTESTS_PROBLEMS:append:riscv32 = " qemu-ptest"

PTESTS_SLOW = "\
    babeltrace-ptest \
    babeltrace2-ptest \
    busybox-ptest \
    coreutils-ptest \
    dbus-ptest \
    e2fsprogs-ptest \
    findutils-ptest \
    glib-2.0-ptest \
    gstreamer1.0-ptest \
    libevent-ptest \
    lttng-tools-ptest \
    openssh-ptest \
    openssl-ptest \
    perl-ptest \
    python3-cryptography-ptest \
    python3-ptest \
    strace-ptest \
    tcl-ptest \
    util-linux-ptest \
    valgrind-ptest \
"

PTESTS_SLOW:remove:riscv64 = "valgrind-ptest"
PTESTS_PROBLEMS:append:riscv64 = "valgrind-ptest"

#    ruby-ptest \ # Timeout
#    lz4-ptest \ # Needs a rewrite
#    rt-tests-ptest \ # Needs to be checked whether it runs at all
#    bash-ptest \ # Test outcomes are non-deterministic by design
#    ifupdown-ptest \ # Tested separately in lib/oeqa/selftest/cases/imagefeatures.py
#    mdadm-ptest \ # Tests rely on non-deterministic sleep() amounts
#    libinput-ptest \ # Tests need an unloaded system to be reliable
#    libpam-ptest \ # Needs pam DISTRO_FEATURE
#    numactl-ptest \ # qemu not (yet) configured for numa; all tests are skipped
#    libseccomp-ptest \ #  tests failed: 38; add to slow tests once addressed
#    python3-numpy-ptest \ # requires even more RAM and (possibly) disk space; multiple failures

PTESTS_PROBLEMS = "\
    ruby-ptest \
    lz4-ptest \
    rt-tests-ptest \
    bash-ptest \
    ifupdown-ptest \
    mdadm-ptest \
    libinput-ptest \
    libpam-ptest \
    libseccomp-ptest \
    numactl-ptest \
    python3-numpy-ptest \
"
