#
# Lists of the ptest in meta-networking, sorted into two sets by the time they take
# Please keep these sorted in alphabetical order
#
# A first pass at getting all meta-networking recipes which inherit ptest
# meta_networking_ptest_recipes=$(bitbake-layers show-recipes --recipes-only --layer meta-networking --inherits ptest --bare | sed -e '1,/=== Matching recipes: ===/d')
# ptests which take less than ~30s each

PTESTS_FAST_META_NETWORKING = "\
    freediameter \
    geoip \
    libcoap \
    libldb \
    libnftnl \
    libtalloc \
    libtdb \
    lldpd \
    mbedtls \
    openhpi \
    squid \
    tcpdump \
"

# firewalld currently hangs forever so disable it for now
#    firewalld
PTESTS_SLOW_META_NETWORKING = "\
    net-snmp \
"

PTESTS_PROBLEMS_META_NETWORKING = "\
    geoip-perl \
    libtevent \
    lksctp-tools \
    nftables \
    openl2tp \
    python3-scapy \
"
