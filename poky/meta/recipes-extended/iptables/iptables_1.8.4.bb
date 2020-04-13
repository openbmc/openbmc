SUMMARY = "Tools for managing kernel packet filtering capabilities"
DESCRIPTION = "iptables is the userspace command line program used to configure and control network packet \
filtering code in Linux."
HOMEPAGE = "http://www.netfilter.org/"
BUGTRACKER = "http://bugzilla.netfilter.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://iptables/iptables.c;beginline=13;endline=25;md5=c5cffd09974558cf27d0f763df2a12dc \
"

SRC_URI = "http://netfilter.org/projects/iptables/files/iptables-${PV}.tar.bz2 \
           file://0001-configure-Add-option-to-enable-disable-libnfnetlink.patch \
           file://0002-configure.ac-only-check-conntrack-when-libnfnetlink-enabled.patch \
           file://iptables.service \
           file://iptables.rules \
           file://ip6tables.service \
           file://ip6tables.rules \
"
SRC_URI[md5sum] = "9b201107957fbf62709c3d8226239b0d"
SRC_URI[sha256sum] = "993a3a5490a544c2cbf2ef15cf7e7ed21af1845baf228318d5c36ef8827e157c"

SYSTEMD_SERVICE_${PN} = "\
    iptables.service \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipv6', 'ip6tables.service', '', d)} \
"

inherit autotools pkgconfig systemd

EXTRA_OECONF = "--with-kernel=${STAGING_INCDIR}"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# libnfnetlink recipe is in meta-networking layer
PACKAGECONFIG[libnfnetlink] = "--enable-libnfnetlink,--disable-libnfnetlink,libnfnetlink libnetfilter-conntrack"

# libnftnl recipe is in meta-networking layer(previously known as libnftables)
PACKAGECONFIG[libnftnl] = "--enable-nftables,--disable-nftables,libnftnl"

do_configure_prepend() {
    # Remove some libtool m4 files
    # Keep ax_check_linker_flags.m4 which belongs to autoconf-archive.
    rm -f libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4
}

IPTABLES_RULES_DIR ?= "${sysconfdir}/${BPN}"

do_install_append() {
    install -d ${D}${IPTABLES_RULES_DIR}
    install -m 0644 ${WORKDIR}/iptables.rules ${D}${IPTABLES_RULES_DIR}

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/iptables.service ${D}${systemd_system_unitdir}

    sed -i \
        -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@RULESDIR@,${IPTABLES_RULES_DIR},g' \
        ${D}${systemd_system_unitdir}/iptables.service

    if ${@bb.utils.contains('PACKAGECONFIG', 'ipv6', 'true', 'false', d)} ; then
        install -m 0644 ${WORKDIR}/ip6tables.rules ${D}${IPTABLES_RULES_DIR}
        install -m 0644 ${WORKDIR}/ip6tables.service ${D}${systemd_system_unitdir}

        sed -i \
            -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@RULESDIR@,${IPTABLES_RULES_DIR},g' \
            ${D}${systemd_system_unitdir}/ip6tables.service
    fi
}

PACKAGES += "${PN}-modules"
PACKAGES_DYNAMIC += "^${PN}-module-.*"

python populate_packages_prepend() {
    modules = do_split_packages(d, '${libdir}/xtables', r'lib(.*)\.so$', '${PN}-module-%s', '${PN} module %s', extra_depends='')
    if modules:
        metapkg = d.getVar('PN') + '-modules'
        d.appendVar('RDEPENDS_' + metapkg, ' ' + ' '.join(modules))
}

RDEPENDS_${PN} = "${PN}-module-xt-standard"
RRECOMMENDS_${PN} = " \
    ${PN}-modules \
    kernel-module-x-tables \
    kernel-module-ip-tables \
    kernel-module-iptable-filter \
    kernel-module-iptable-nat \
    kernel-module-nf-defrag-ipv4 \
    kernel-module-nf-conntrack \
    kernel-module-nf-conntrack-ipv4 \
    kernel-module-nf-nat \
    kernel-module-ipt-masquerade \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipv6', '\
        kernel-module-ip6table-filter \
        kernel-module-ip6-tables \
    ', '', d)} \
"

FILES_${PN} += "${datadir}/xtables"

# Include the symlinks as well in respective packages
FILES_${PN}-module-xt-conntrack += "${libdir}/xtables/libxt_state.so"
FILES_${PN}-module-xt-ct += "${libdir}/xtables/libxt_NOTRACK.so"

ALLOW_EMPTY_${PN}-modules = "1"

INSANE_SKIP_${PN}-module-xt-conntrack = "dev-so"
INSANE_SKIP_${PN}-module-xt-ct = "dev-so"
