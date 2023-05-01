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
    libssh \
    libtinyxml2 \
    libxml++-5.0 \
    libxmlb \
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
"
PTESTS_FAST_META_OE:remove:libc-musl = "minicoredumper"

PTESTS_SLOW_META_OE = "\
    fftw \
    libusb-compat \
    rsyslog \
"

PTESTS_PROBLEMS_META_OE ="\
    jemalloc \
    kernel-selftest \
    keyutils \
    libdbi-perl \
    libgpiod \
    libjcat \
    libteam \
    libxml++ \
    mcelog \
    oprofile \
    ostree \
    psqlodbc \
    pv \
"
