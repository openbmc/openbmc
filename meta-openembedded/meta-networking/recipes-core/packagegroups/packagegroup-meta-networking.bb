SUMMARY = "Meta-networking packagegroups"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-networking \
    packagegroup-meta-networking-connectivity \
    packagegroup-meta-networking-daemons  \
    packagegroup-meta-networking-devtools \
    packagegroup-meta-networking-extended \
    packagegroup-meta-networking-filter \
    packagegroup-meta-networking-irc \
    packagegroup-meta-networking-kernel \
    packagegroup-meta-networking-netkit \
    packagegroup-meta-networking-protocols \
    packagegroup-meta-networking-support \
'

RDEPENDS_packagegroup-meta-networking = "\
    packagegroup-meta-networking-connectivity \
    packagegroup-meta-networking-daemons  \
    packagegroup-meta-networking-devtools \
    packagegroup-meta-networking-extended \
    packagegroup-meta-networking-filter \
    packagegroup-meta-networking-irc \
    packagegroup-meta-networking-kernel \
    packagegroup-meta-networking-netkit \
    packagegroup-meta-networking-protocols \
    packagegroup-meta-networking-support \
    "

RDEPENDS_packagegroup-meta-networking-connectivity = "\
    crda \
    daq \
    adcli \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluetooth x11", "blueman", "", d)} \
    cannelloni \
    civetweb \
    libdnet \
    dibbler-client \
    dibbler-relay \
    dibbler-server \
    relayd \
    lftp \
    sethdlc \
    snort \
    ufw \
    vlan \
    vpnc \
    ez-ipupdate \
    firewalld \
    freeradius \
    mbedtls \
    miniupnpd \
    mosquitto \
    nanomsg \
    nng \
    netplan \
    networkmanager-openvpn \
    networkmanager \
    openconnect \
    python3-networkmanager \
    rdate \
    rdist \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "samba", "", d)} \
    wolfssl \
    autossh \
    bearssl \
"

RDEPENDS_packagegroup-meta-networking-connectivity_remove_libc-musl = "rdist"

RDEPENDS_packagegroup-meta-networking-daemons = "\
    atftp \
    autofs \
    cyrus-sasl \
    ippool \
    iscsi-initiator-utils \
    lldpd \
    ncftp \
    igmpproxy \
    postfix \
    proftpd \
    ptpd \
    pure-ftpd \
    radvd \
    squid \
    tftp-hpa \
    tftp-hpa-server \
    vblade \
    vsftpd \
    keepalived \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "networkd-dispatcher", "", d)} \
    openhpi \
    opensaf \
"

RDEPENDS_packagegroup-meta-networking-daemons_remove_libc-musl = "opensaf"

RDEPENDS_packagegroup-meta-networking-devtools = "\
    python3-ldap \
"

RDEPENDS_packagegroup-meta-networking-extended = "\
    corosync \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "dlm", "", d)} \
    kronosnet \
"

RDEPENDS_packagegroup-meta-networking-filter = "\
    libnftnl \
    conntrack-tools \
    ebtables \
    ipset \
    libnetfilter-acct \
    libnetfilter-conntrack \
    libnetfilter-cthelper \
    libnetfilter-cttimeout \
    libnetfilter-log \
    libnetfilter-queue \
    libnfnetlink \
    arno-iptables-firewall \
    nfacct \
    nftables \
"

RDEPENDS_packagegroup-meta-networking-irc = "\
    weechat \
    znc \
"

RDEPENDS_packagegroup-meta-networking-kernel = "\
    wireguard-tools \
"

RDEPENDS_packagegroup-meta-networking-netkit = "\
    netkit-rwho-client \
    netkit-rwho-server \
    netkit-rsh-client \
    netkit-rsh-server \
    netkit-telnet \
    netkit-tftp-client \
    netkit-tftp-server \
    netkit-ftp \
    netkit-rpc \
    "

RDEPENDS_packagegroup-meta-networking-netkit_remove_libc-musl = " \
    netkit-rsh-client netkit-rsh-server netkit-telnet"

RDEPENDS_packagegroup-meta-networking-protocols = "\
    babeld \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "dante", "", d)} \
    freediameter \
    net-snmp \
    openflow \
    openflow \
    openl2tp \
    mdns \
    nopoll \
    quagga \
    radiusclient-ng \
    tsocks \
    openlldp \
    zeroconf \
    pptp-linux \
    rp-pppoe \
    usrsctp \
    xl2tpd \
"

RDEPENDS_packagegroup-meta-networking-protocols_remove_libc-musl = "mdns"

RDEPENDS_packagegroup-meta-networking-support = "\
    aoetools \
    arptables \
    bridge-utils \
    celt051 \
    cim-schema-docs \
    cim-schema-final \
    cifs-utils \
    dnsmasq \
    curlpp \
    drbd-utils \
    dovecot \
    fping \
    esmtp \
    fetchmail \
    geoip-perl \
    geoip \
    geoipupdate \
    fwknop \
    htpdate \
    iftop \
    ifmetric \
    ipvsadm \
    libesmtp \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "libldb", "", d)} \
    libmemcached \
    libtalloc \
    ipcalc \
    libtevent \
    linux-atm \
    lksctp-tools \
    memcached \
    ifenslave \
    netcat \
    netcat-openbsd \
    libtdb \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "non-commercial", "netperf", "", d)} \
    yp-tools \
    ypbind-mt \
    yp-tools \
    mtr \
    ntp ntpdate sntp ntpdc ntpq ntp-tickadj ntp-utils \
    nbd-client \
    nbd-server \
    nbd-trdump \
    openvpn \
    macchanger \
    nbdkit \
    ssmping \
    libmaxminddb \
    libowfat \
    ncp \
    strongswan \
    ndisc6 \
    tcpdump \
    tcpslice \
    netcf \
    nghttp2 \
    tnftp \
    traceroute \
    tunctl \
    wireshark \
    ndpi \
    ntopng \
    nuttcp \
    nvmetcli \
    open-isns \
    openipmi \
    phytool \
    pimd \
    ruli \
    smcroute \
    ${@bb.utils.contains_any("TRANSLATED_TARGET_ARCH", "i586 x86-64", "spice-protocol spice", "", d)} \
    usbredir \
    ssmtp \
    stunnel \
    rdma-core \
    tcpreplay \
    tinyproxy \
    uftp \
    unbound \
    vnstat \
    wpan-tools \
    ettercap \
"
RDEPENDS_packagegroup-meta-networking-support_remove_mipsarch = "memcached"

EXCLUDE_FROM_WORLD = "1"
# Empty packages, only devel headers and libs
# nngpp
# Use ntp and not chrony or ntimed
