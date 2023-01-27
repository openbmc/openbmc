#
# Lists of the ptests in OE-Core, sorted into two sets by the time they take
#
# ptests which take less than ~30s each
#
PTESTS_FAST = "\
    acl-ptest \
    apr-util-ptest \
    attr-ptest \
    babeltrace-ptest \
    babeltrace2-ptest \
    bc-ptest \
    bluez5-ptest \
    busybox-ptest \
    diffstat-ptest \
    diffutils-ptest \
    ethtool-ptest \
    expat-ptest \
    findutils-ptest \
    flex-ptest \
    gawk-ptest \
    gdbm-ptest \
    gdk-pixbuf-ptest \
    glib-networking-ptest \
    gzip-ptest \
    json-c-ptest \
    json-glib-ptest \
    libconvert-asn1-perl-ptest \
    liberror-perl-ptest \
    libgpg-error-ptest\
    libnl-ptest \
    libpcre-ptest \
    libssh2-ptest \
    libtimedate-perl-ptest \
    libtest-fatal-perl-ptest \
    libtest-needs-perl-ptest \
    libtest-warnings-perl-ptest \
    libtry-tiny-perl-ptest \
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
    opkg-ptest \
    pango-ptest \
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
    apr-ptest \
    bzip2-ptest \
    coreutils-ptest \
    curl-ptest \
    dbus-ptest \
    e2fsprogs-ptest \
    elfutils-ptest \
    gettext-ptest \
    glib-2.0-ptest \
    gstreamer1.0-ptest \
    libevent-ptest \
    libgcrypt-ptest \
    libmodule-build-perl-ptest \
    lttng-tools-ptest \
    openssh-ptest \
    openssl-ptest \
    parted-ptest \
    perl-ptest \
    python3-cryptography-ptest \
    python3-ptest \
    strace-ptest \
    tcl-ptest \
    util-linux-ptest \
    valgrind-ptest \
"

PTESTS_SLOW:remove:riscv64 = "valgrind-ptest"
PTESTS_PROBLEMS:append:riscv64 = " valgrind-ptest"
PTESTS_SLOW:append:libc-musl = " libc-test-ptest"
PTESTS_SLOW:remove:x86 = "valgrind-ptest"
PTESTS_PROBLEMS:append:x86 = " valgrind-ptest"

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
