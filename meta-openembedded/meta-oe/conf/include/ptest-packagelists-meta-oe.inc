#
# Lists of the ptest in meta-oe, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-oe recipes which inherit ptest
# meta_oe_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-oe --inherits ptest --bare | tr '\n' ' ' | pcregrep -o1 '^NOTE:.+===(.+)$')
#
# ptests which take less than ~30s each
PTESTS_FAST_META_OE = "\
    cmocka \
    cunit \
    duktape \
    fmt \
    function2 \
    fwupd \
    gcab \
    jq \
    leveldb \
    libcereal \
    libee \
    libio-pty-perl \
    libmanette \
    librelp \
    libssh \
    libtinyxml2 \
    libxml++-5.0 \
    libyang \
    lmdb \
    minicoredumper \
    neon \
    nodejs \
    onig \
    poco \
    protobuf \
    sdbus-c++ \
    uthash \
    xmlsec1 \
    zeromq \
    cjson \
"
PTESTS_FAST_META_OE:append:x86 = " mcelog"
PTESTS_FAST_META_OE:append:x86-64 = " mcelog"

PTESTS_FAST_META_OE:remove:libc-musl = "minicoredumper"
PTESTS_FAST_META_OE:remove:riscv64 = "nodejs"
PTESTS_FAST_META_OE:remove:riscv32 = "nodejs"

PTESTS_SLOW_META_OE = "\
    fftw \
    libusb-compat \
    rsyslog \
    mariadb \
    re2 \
"
PTESTS_SLOW_META_OE:append:x86 = " kernel-selftest"
PTESTS_SLOW_META_OE:append:x86-64 = " kernel-selftest"
PTESTS_SLOW_META_OE:remove:libc-musl = "kernel-selftest"

PTESTS_PROBLEMS_META_OE ="\
    jemalloc \
    keyutils \
    libdbi-perl \
    libgpiod \
    libjcat \
    libteam \
    libxml++ \
    oprofile \
    ostree \
    psqlodbc \
    pv \
"
