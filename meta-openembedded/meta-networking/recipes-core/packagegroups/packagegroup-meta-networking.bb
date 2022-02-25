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

RDEPENDS:packagegroup-meta-networking = "\
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

RDEPENDS:packagegroup-meta-networking-connectivity = "\
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
    dhcp-relay \
"

RDEPENDS:packagegroup-meta-networking-connectivity:remove:libc-musl = "rdist"

RDEPENDS:packagegroup-meta-networking-daemons = "\
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

RDEPENDS:packagegroup-meta-networking-daemons:remove:libc-musl = "opensaf"

RDEPENDS:packagegroup-meta-networking-devtools = "\
    python3-ldap \
    python3-scapy \
"

RDEPENDS:packagegroup-meta-networking-extended = "\
    corosync \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "dlm", "", d)} \
    kronosnet \
"

RDEPENDS:packagegroup-meta-networking-filter = "\
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

RDEPENDS:packagegroup-meta-networking-irc = "\
    weechat \
    znc \
"

RDEPENDS:packagegroup-meta-networking-kernel = "\
    wireguard-tools \
"

RDEPENDS:packagegroup-meta-networking-netkit = "\
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

RDEPENDS:packagegroup-meta-networking-netkit:remove:libc-musl = " \
    netkit-rsh-client netkit-rsh-server netkit-telnet"

RDEPENDS:packagegroup-meta-networking-protocols = "\
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

RDEPENDS:packagegroup-meta-networking-support = "\
    aoetools \
    arptables \
    bmon \
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
    http-parser \
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
    mctp \
    memcached \
    ifenslave \
    netcat \
    netcat-openbsd \
    libtdb \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "non-commercial", "netperf", "", d)} \
    yp-tools \
    ypbind-mt \
    yp-tools \
    mtr \
    netsniff-ng \
    ntp ntpdate sntp ntpdc ntpq ntp-tickadj ntp-utils \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "ntpsec", "", d)} \
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
RDEPENDS:packagegroup-meta-networking-support:remove:mipsarch = "memcached"

EXCLUDE_FROM_WORLD = "1"
# Empty packages, only devel headers and libs
# nngpp
# Use ntp and not chrony or ntimed
