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
    flex-ptest \
    gawk-ptest \
    gdbm-ptest \
    gdk-pixbuf-ptest \
    gettext-ptest \
    gzip-ptest \
    json-glib-ptest \
    kbd-ptest \
    libconvert-asn1-perl-ptest \
    liberror-perl-ptest \
    libmodule-build-perl-ptest \
    libpcre-ptest \
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
    lzo-ptest \
    m4-ptest \
    nettle-ptest \
    openssl-ptest \
    opkg-ptest \
    pango-ptest \
    parted-ptest \
    quilt-ptest \
    sed-ptest \
    slang-ptest \
    wayland-ptest \
    zlib-ptest \
"

#PTESTS_PROBLEMS = "\
#    qemu-ptest \ # Doesn't run any tests?
#    ruby-ptest \ # Timeout
#    clutter-1.0-ptest \ # Doesn't build due to depends on cogl-1.0
#    lz4-ptest \ # Needs a rewrite
#    rt-tests-ptest \ # Needs to be checked whether it runs at all
#    bash-ptest \ # Test outcomes are non-deterministic by design
#    ifupdown-ptest \ # Tested separately in lib/oeqa/selftest/cases/imagefeatures.py
#    mdadm-ptest \ # Tests rely on non-deterministic sleep() amounts
#    libinput-ptest \ # Tests need an unloaded system to be reliable
#"

PTESTS_SLOW = "\
    babeltrace-ptest \
    babeltrace2-ptest \
    busybox-ptest \
    coreutils-ptest \
    dbus-test-ptest \
    e2fsprogs-ptest \
    glib-2.0-ptest \
    gstreamer1.0-ptest \
    libevent-ptest \
    lttng-tools-ptest \
    openssh-ptest \
    openssl-ptest \
    perl-ptest \
    python3-ptest \
    strace-ptest \
    tcl-ptest \
    util-linux-ptest \
    valgrind-ptest \
"

PTESTS_SLOW_remove_riscv64 = "valgrind-ptest"
