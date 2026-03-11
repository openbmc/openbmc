#
# Lists of the ptest in meta-oe, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-oe recipes which inherit ptest
# meta_oe_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-oe --inherits ptest --bare | tr '\n' ' ' | pcregrep -o1 '^NOTE:.+===(.+)$')
#
# ptests which take less than ~30s each
PTESTS_FAST_META_OE = "\
    asio \
    cmocka \
    cunit \
    duktape \
    fuse3 \
    function2 \
    fwupd \
    gcab \
    jemalloc \
    jq \
    leveldb \
    libcereal \
    libgpiod \
    libjcat \
    libio-pty-perl \
    libmanette \
    librelp \
    libssh \
    libteam \
    libtinyxml2 \
    libtoml11 \
    libxml++-5.0 \
    libyang \
    lmdb \
    microsoft-gsl \
    minicoredumper \
    minizip-ng \
    neon \
    nlohmann-json \
    nodejs \
    onig \
    oprofile \
    pegtl \
    protobuf \
    pv \
    sdbus-c++ \
    tomlplusplus \
    uthash \
    xmlsec1 \
    zeromq \
    cjson \
"
PTESTS_FAST_META_OE:append:x86 = " mcelog"
PTESTS_FAST_META_OE:append:x86-64 = " mcelog"

PTESTS_FAST_META_OE:remove:libc-musl = "jemalloc minicoredumper oprofile"
PTESTS_FAST_META_OE:remove:riscv64 = "nodejs oprofile"
PTESTS_FAST_META_OE:remove:riscv32 = "nodejs oprofile"

PTESTS_SLOW_META_OE = "\
    c-ares \
    fftw \
    libusb-compat \
    rocksdb \
    re2 \
"
PTESTS_SLOW_META_OE:append:x86 = " kernel-selftest"
PTESTS_SLOW_META_OE:append:x86-64 = " kernel-selftest"
PTESTS_SLOW_META_OE:remove:libc-musl = "kernel-selftest"

PTESTS_PROBLEMS_META_OE = "\
    keyutils \
    libdbi-perl \
    mariadb \
    ostree \
    poco \
    psqlodbc \
    rsyslog \
"
PTESTS_PROBLEMS_META_OE:append:libc-musl = " jemalloc minicoredumper oprofile"
