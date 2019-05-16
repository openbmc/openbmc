SUMMARY = "Tools for managing kernel packet filtering capabilities"
DESCRIPTION = "iptables is the userspace command line program used to configure and control network packet \
filtering code in Linux."
HOMEPAGE = "http://www.netfilter.org/"
BUGTRACKER = "http://bugzilla.netfilter.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263\
                    file://iptables/iptables.c;beginline=13;endline=25;md5=c5cffd09974558cf27d0f763df2a12dc"

SRC_URI = "http://netfilter.org/projects/iptables/files/iptables-${PV}.tar.bz2 \
           file://0001-configure-Add-option-to-enable-disable-libnfnetlink.patch \
           file://0002-configure.ac-only-check-conntrack-when-libnfnetlink-enabled.patch \
           file://0003-extensions-format-security-fixes-in-libipt_icmp.patch  \
"

SRC_URI[md5sum] = "944558e88ddcc3b9b0d9550070fa3599"
SRC_URI[sha256sum] = "a3778b50ed1a3256f9ca975de82c2204e508001fc2471238c8c97f3d1c4c12af"

inherit autotools pkgconfig

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

PACKAGES += "${PN}-modules"
PACKAGES_DYNAMIC += "^${PN}-module-.*"

python populate_packages_prepend() {
    modules = do_split_packages(d, '${libdir}/xtables', r'lib(.*)\.so$', '${PN}-module-%s', '${PN} module %s', extra_depends='')
    if modules:
        metapkg = d.getVar('PN') + '-modules'
        d.appendVar('RDEPENDS_' + metapkg, ' ' + ' '.join(modules))
}

FILES_${PN} += "${datadir}/xtables"

ALLOW_EMPTY_${PN}-modules = "1"

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
"
