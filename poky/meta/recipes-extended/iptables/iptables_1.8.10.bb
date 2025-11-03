SUMMARY = "Tools for managing kernel packet filtering capabilities"
DESCRIPTION = "iptables is the userspace command line program used to configure and control network packet \
filtering code in Linux."
HOMEPAGE = "http://www.netfilter.org/"
BUGTRACKER = "http://bugzilla.netfilter.org/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://iptables/iptables.c;beginline=13;endline=25;md5=c5cffd09974558cf27d0f763df2a12dc \
"

SRC_URI = "http://netfilter.org/projects/iptables/files/iptables-${PV}.tar.xz \
           file://iptables.service \
           file://iptables.rules \
           file://ip6tables.service \
           file://ip6tables.rules \
           file://0001-configure-Add-option-to-enable-disable-libnfnetlink.patch \
           file://0002-iptables-xshared.h-add-missing-sys.types.h-include.patch \
           file://0004-configure.ac-only-check-conntrack-when-libnfnetlink-.patch \
           file://0005-nft-ruleparse-Add-missing-braces-around-ternary.patch \
           "
SRC_URI[sha256sum] = "5cc255c189356e317d070755ce9371eb63a1b783c34498fb8c30264f3cc59c9c"

SYSTEMD_SERVICE:${PN} = "\
    iptables.service \
    ${@bb.utils.contains('PACKAGECONFIG', 'ipv6', 'ip6tables.service', '', d)} \
"

inherit autotools pkgconfig systemd

EXTRA_OECONF = "--with-kernel=${STAGING_INCDIR}"

CFLAGS:append:libc-musl = " -D__UAPI_DEF_ETHHDR=0"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# libnfnetlink recipe is in meta-networking layer
PACKAGECONFIG[libnfnetlink] = "--enable-libnfnetlink,--disable-libnfnetlink,libnfnetlink libnetfilter-conntrack"

# libnftnl recipe is in meta-networking layer(previously known as libnftables)
PACKAGECONFIG[libnftnl] = "--enable-nftables,--disable-nftables,libnftnl"

do_configure:prepend() {
    # Remove some libtool m4 files
    # Keep ax_check_linker_flags.m4 which belongs to autoconf-archive.
    rm -f libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4

    # Copy a header to fix out of tree builds
    cp -f ${S}/libiptc/linux_list.h ${S}/include/libiptc/
}

IPTABLES_RULES_DIR ?= "${sysconfdir}/${BPN}"

do_install:append() {
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

    # if libnftnl is included, make the iptables symlink point to the nft-based binary by default
    if ${@bb.utils.contains('PACKAGECONFIG', 'libnftnl', 'true', 'false', d)} ; then
        ln -sf ${sbindir}/xtables-nft-multi ${D}${sbindir}/iptables 
        ln -sf ${sbindir}/xtables-nft-multi ${D}${sbindir}/iptables-save
        ln -sf ${sbindir}/xtables-nft-multi ${D}${sbindir}/iptables-restore
        # ethertypes is provided by the netbase package
        rm -f ${D}${sysconfdir}/ethertypes
    fi
}

PACKAGES =+ "${PN}-modules ${PN}-apply"
PACKAGES_DYNAMIC += "^${PN}-module-.*"

python populate_packages:prepend() {
    modules = do_split_packages(d, '${libdir}/xtables', r'lib(.*)\.so$', '${PN}-module-%s', '${PN} module %s', extra_depends='')
    if modules:
        metapkg = d.getVar('PN') + '-modules'
        d.appendVar('RDEPENDS:' + metapkg, ' ' + ' '.join(modules))
}

RDEPENDS:${PN} = "${PN}-module-xt-standard"
RRECOMMENDS:${PN} = " \
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

FILES:${PN} += "${datadir}/xtables"

FILES:${PN}-apply = "${sbindir}/ip*-apply"
RDEPENDS:${PN}-apply = "${PN} bash"

# Include the symlinks as well in respective packages
FILES:${PN}-module-xt-conntrack += "${libdir}/xtables/libxt_state.so"
FILES:${PN}-module-xt-ct += "${libdir}/xtables/libxt_NOTRACK.so ${libdir}/xtables/libxt_REDIRECT.so"
FILES:${PN}-module-xt-nat += "${libdir}/xtables/libxt_SNAT.so ${libdir}/xtables/libxt_DNAT.so ${libdir}/xtables/libxt_MASQUERADE.so"

ALLOW_EMPTY:${PN}-modules = "1"

INSANE_SKIP:${PN}-module-xt-conntrack = "dev-so"
INSANE_SKIP:${PN}-module-xt-ct = "dev-so"
INSANE_SKIP:${PN}-module-xt-nat = "dev-so"
