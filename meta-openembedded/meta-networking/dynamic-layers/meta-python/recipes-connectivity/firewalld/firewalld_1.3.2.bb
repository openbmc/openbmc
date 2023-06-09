SUMMARY = "Dynamic firewall daemon with a D-Bus interface"
HOMEPAGE = "https://firewalld.org/"
BUGTRACKER = "https://github.com/firewalld/firewalld/issues"
UPSTREAM_CHECK_URI = "https://github.com/firewalld/firewalld/releases"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "\
    https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.bz2 \
    file://firewalld.init \
    file://run-ptest \
"
SRC_URI[sha256sum] = "aba0d8ce9617b906ea4866bf0bdfb2c2d5312f53b8e9e8e9e4d49bf330da5b5e"

# glib-2.0-native is needed for GSETTINGS_RULES autoconf macro from gsettings.m4
DEPENDS = "intltool-native glib-2.0-native nftables"

inherit gettext autotools-brokensep bash-completion pkgconfig python3native python3-dir gsettings systemd update-rc.d ptest features_check

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemd-unitdir=${systemd_system_unitdir},--disable-systemd"
PACKAGECONFIG[docs] = "--with-xml-catalog=${STAGING_ETCDIR_NATIVE}/xml/catalog,--disable-docs,libxslt-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[ipset] = "--with-ipset=${sbindir}/ipset,--without-ipset,,ipset"
PACKAGECONFIG[ebtables] = "--with-ebtables=${base_sbindir}/ebtables --with-ebtables-restore=${sbindir}/ebtables-legacy-restore,--without-ebtables --without-ebtables-restore,,ebtables"

# Default logging configuration: mixed syslog file console
FIREWALLD_DEFAULT_LOG_TARGET ??= "syslog"

# The UIs are not yet tested and the dependencies are probably not quite correct yet.
# Splitting into separate packages is beneficial so that no dead code is transferred
# to the target device.
# Without enabling qt5, the firewalld-config package is not usable.
# Without enabling qt5 and gtk, the firewalld-applet package is not usable.
PACKAGECONFIG[qt5] = ""
PACKAGECONFIG[gtk] = ""

PACKAGES =+ "python3-firewall ${PN}-applet ${PN}-config ${PN}-offline-cmd ${PN}-zsh-completion ${PN}-log-rotate"

# iptables, ip6tables, ebtables, and ipset *should* be unnecessary
# when the nftables backend is available, because nftables supersedes all of them.
# However we still need iptables and ip6tables to be available otherwise any
# application relying on "direct passthrough" rules (such as docker) will break.
# /etc/sysconfig/firewalld is a Red Hat-ism, only referenced by
# the Red Hat-specific init script which we aren't using, so we disable that.
EXTRA_OECONF = "\
    --with-iptables=${sbindir}/iptables \
    --with-iptables-restore=${sbindir}/iptables-restore \
    --with-ip6tables=${sbindir}/ip6tables \
    --with-ip6tables-restore=${sbindir}/ip6tables-restore \
    --disable-sysconfig \
"

INITSCRIPT_NAME = "firewalld"
SYSTEMD_SERVICE:${PN} = "firewalld.service"

# kernel modules loaded after ptest execution (linux-yocto 5.15)
FIREWALLD_KERNEL_MODULES ?= "\
    xt_tcpudp \
    xt_TCPMSS \
    xt_set \
    xt_sctp \
    xt_REDIRECT \
    xt_pkttype \
    xt_NFLOG \
    xt_nat \
    xt_MASQUERADE \
    xt_mark \
    xt_mac \
    xt_LOG \
    xt_limit \
    xt_dccp \
    xt_CT \
    xt_conntrack \
    xt_CHECKSUM \
    nft_redir \
    nft_objref \
    nft_nat \
    nft_masq \
    nft_log \
    nfnetlink_log \
    nf_nat_tftp \
    nf_nat_sip \
    nf_nat_ftp \
    nf_log_syslog \
    nf_conntrack_tftp \
    nf_conntrack_sip \
    nf_conntrack_netbios_ns \
    nf_conntrack_ftp \
    nf_conntrack_broadcast \
    ipt_REJECT \
    ip6t_rpfilter \
    ip6t_REJECT \
    ip_set_hash_netport \
    ip_set_hash_netnet \
    ip_set_hash_netiface \
    ip_set_hash_net \
    ip_set_hash_mac \
    ip_set_hash_ipportnet \
    ip_set_hash_ipport \
    ip_set_hash_ipmark \
    ip_set_hash_ip \
    ebt_ip6 \
    nft_fib_inet \
    nft_fib_ipv4 \
    nft_fib_ipv6 \
    nft_fib \
    nft_reject_inet \
    nf_reject_ipv4 \
    nf_reject_ipv6 \
    nft_reject \
    nft_ct \
    nft_chain_nat \
    ebtable_nat \
    ebtable_broute \
    ip6table_nat \
    ip6table_mangle \
    ip6table_raw \
    ip6table_security \
    iptable_nat \
    nf_nat \
    nf_conntrack \
    nf_defrag_ipv6 \
    nf_defrag_ipv4 \
    iptable_mangle \
    iptable_raw \
    iptable_security \
    ip_set \
    ebtable_filter \
    ebtables \
    ip6table_filter \
    ip6_tables \
    iptable_filter \
    ip_tables \
    x_tables \
    sch_fq_codel \
"

do_configure:prepend() {
    export DEFAULT_LOG_TARGET=${FIREWALLD_DEFAULT_LOG_TARGET}
}

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'false', 'true', d)}; then
        # firewalld ships an init script but it contains Red Hat-isms, replace it with our own
        rm -rf ${D}${sysconfdir}/rc.d/
        install -d ${D}${sysconfdir}/init.d
        install -m0755 ${WORKDIR}/firewalld.init ${D}${sysconfdir}/init.d/firewalld
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'polkit', 'false', 'true', d)}; then
        # Delete polkit profiles if polkit is not available
        rm -rf ${D}${datadir}/polkit-1
    fi

    # We ran ./configure with PYTHON pointed at the binary inside $STAGING_BINDIR_NATIVE
    # so now we need to fix up any references to point at the proper path in the image.
    # This hack is also in distutils.bbclass, but firewalld doesn't use distutils/setuptools.
    if [ ${PN} != "${BPN}-native" ]; then
        sed -i -e s:${STAGING_BINDIR_NATIVE}/python3-native/python3:${bindir}/python3:g \
            ${D}${bindir}/* ${D}${sbindir}/* ${D}${sysconfdir}/firewalld/*.xml
    fi
    sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g \
        ${D}${bindir}/* ${D}${sbindir}/* ${D}${sysconfdir}/firewalld/*.xml

    # This file contains Red Hat-isms. Modules get loaded without it.
    rm -f ${D}${sysconfdir}/modprobe.d/firewalld-sysctls.conf
}

do_install_ptest:append() {
    # Add kernel modules to the ptest script
    if [ ${PTEST_ENABLED} = "1" ]; then
        sed -i -e 's:@@FIREWALLD_KERNEL_MODULES@@:${FIREWALLD_KERNEL_MODULES}:g' \
            ${D}${PTEST_PATH}/run-ptest
    fi
}

SUMMARY:python3-firewall = "${SUMMARY} (Python3 bindings)"
FILES:python3-firewall = "\
    ${PYTHON_SITEPACKAGES_DIR}/firewall/__pycache__/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/config/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/config/__pycache__/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/core/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/core/__pycache__/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/core/io/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/core/io/__pycache__/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/server/*.py* \
    ${PYTHON_SITEPACKAGES_DIR}/firewall/server/__pycache__/*.py* \
"
RDEPENDS:python3-firewall = "\
    python3-dbus \
    nftables-python \
    python3-pygobject \
"

# Do not depend on QT5 layer and GTK deps if not explicitely required.
FIREWALLD_QT5_RDEPENDS = "\
    ${PN}-config \
    hicolor-icon-theme \
    python3-pyqt5 \
    python3-pygobject \
    libnotify \
    networkmanager \
"
FIREWALLD_GTK_RDEPENDS = "\
    gtk3 \
"

# A QT5 based UI
SUMMARY:${PN}-config = "${SUMMARY} (configuration application)"
FILES:${PN}-config = "\
    ${bindir}/firewall-config \
    ${datadir}/firewalld/firewall-config.glade \
    ${datadir}/firewalld/gtk3_chooserbutton.py* \
    ${datadir}/firewalld/gtk3_niceexpander.py* \
    ${datadir}/applications/firewall-config.desktop \
    ${datadir}/metainfo/firewall-config.appdata.xml \
    ${datadir}/icons/hicolor/*/apps/firewall-config*.* \
"
RDEPENDS:${PN}-config += "\
    python3-core \
    python3-ctypes \
    ${@bb.utils.contains('PACKAGECONFIG', 'qt5', '${FIREWALLD_QT5_RDEPENDS}', '', d)} \
"

# A GTK3 applet depending on the QT5 firewall-config UI
SUMMARY:${PN}-applet = "${SUMMARY} (panel applet)"
FILES:${PN}-applet += "\
    ${bindir}/firewall-applet \
    ${sysconfdir}/xdg/autostart/firewall-applet.desktop \
    ${sysconfdir}/firewall/applet.conf \
    ${datadir}/icons/hicolor/*/apps/firewall-applet*.* \
"
RDEPENDS:${PN}-applet += "\
    python3-core \
    python3-ctypes \
    ${@bb.utils.contains('PACKAGECONFIG', 'qt5', '${FIREWALLD_QT5_RDEPENDS}', '', d)} \
    ${@bb.utils.contains('PACKAGECONFIG', 'gtk', '${FIREWALLD_GTK_RDEPENDS}', '', d)} \
"

SUMMARY:${PN}-offline-cmd = "${SUMMARY} (offline configuration utility)"
FILES:${PN}-offline-cmd += " \
    ${bindir}/firewall-offline-cmd \
"
RDEPENDS:${PN}-offline-cmd += "python3-core"

SUMMARY:${PN}-log-rotate = "${SUMMARY} (log-rotate configuration)"
FILES:${PN}-log-rotate += "${sysconfdir}/logrotate.d"

# To get allmost all tests passing
# - Enable PACKAGECONFIG ipset, ebtable
# - Enough RAM QB_MEM = "-m 8192" (used für fancy ipset tests)
FILES:${PN}-ptest += "\
    ${datadir}/firewalld/testsuite \
"
RDEPENDS:${PN}-ptest += "\
    python3-unittest \
    ${PN}-offline-cmd \
    procps-ps \
    iproute2 \
"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-utils glibc-localedata-en-us"

FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"

FILES:${PN} += "\
    ${PYTHON_SITEPACKAGES_DIR}/firewall \
    ${nonarch_libdir}/firewalld \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
    ${datadir}/metainfo \
    ${datadir}/glib-2.0/schemas/org.fedoraproject.FirewallConfig.gschema.xml \
"
RDEPENDS:${PN} += "\
    python3-firewall \
    iptables \
    python3-core \
    python3-io \
    python3-fcntl \
    python3-syslog \
    python3-xml \
    python3-json \
    python3-ctypes \
    python3-pprint \
"
# If firewalld writes a log file rotation is needed
RRECOMMENDS:${PN} += "${@bb.utils.contains_any('FIREWALLD_DEFAULT_LOG_TARGET', [ 'mixed', 'file' ], '${PN}-log-rotate', '', d)}"

# Add required kernel modules. With Yocto kernel 5.15 this currently means:
# - features/nf_tables/nf_tables.scc
# - features/netfilter/netfilter.scc
# - cgl/features/audit/audit.scc
# - cfg/net/ip6_nf.scc
# - Plus:
#   - ebtables
#   - ipset
#   - CONFIG_IP6_NF_SECURITY=m
#   - CONFIG_IP6_NF_MATCH_RPFILTER=m
#   - CONFIG_IP6_NF_TARGET_REJECT=m
#   - CONFIG_NFT_OBJREF=m
#   - CONFIG_NFT_FIB=m
#   - CONFIG_NFT_FIB_INET=m
#   - CONFIG_NFT_FIB_IPV4=m
#   - CONFIG_NFT_FIB_IPV6=m
#   - CONFIG_NETFILTER_XT_TARGET_CHECKSUM=m
#   - CONFIG_NETFILTER_XT_SET=m
def get_kernel_deps(d):
    kmodules = (d.getVar('FIREWALLD_KERNEL_MODULES') or "").split()
    return ' '.join([ 'kernel-module-' + mod.replace('_', '-').lower() for mod in kmodules ])
RRECOMMENDS:${PN} += "${@get_kernel_deps(d)}"
