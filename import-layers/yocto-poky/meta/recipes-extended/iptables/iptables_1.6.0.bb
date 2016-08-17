SUMMARY = "Tools for managing kernel packet filtering capabilities"
DESCRIPTION = "iptables is the userspace command line program used to configure and control network packet \
filtering code in Linux."
HOMEPAGE = "http://www.netfilter.org/"
BUGTRACKER = "http://bugzilla.netfilter.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263\
                    file://iptables/iptables.c;beginline=13;endline=25;md5=c5cffd09974558cf27d0f763df2a12dc"

RRECOMMENDS_${PN} = "kernel-module-x-tables \
                     kernel-module-ip-tables \
                     kernel-module-iptable-filter \
                     kernel-module-iptable-nat \
                     kernel-module-nf-defrag-ipv4 \
                     kernel-module-nf-conntrack \
                     kernel-module-nf-conntrack-ipv4 \
                     kernel-module-nf-nat \
                     kernel-module-ipt-masquerade"
FILES_${PN} =+ "${libdir}/xtables/ ${datadir}/xtables"

SRC_URI = "http://netfilter.org/projects/iptables/files/iptables-${PV}.tar.bz2 \
           file://types.h-add-defines-that-are-required-for-if_packet.patch \
           file://0001-configure-Add-option-to-enable-disable-libnfnetlink.patch \
           file://0002-configure.ac-only-check-conntrack-when-libnfnetlink-enabled.patch \
          "
SRC_URI_append_libc-musl = " file://0001-fix-build-with-musl.patch"

SRC_URI[md5sum] = "27ba3451cb622467fc9267a176f19a31"
SRC_URI[sha256sum] = "4bb72a0a0b18b5a9e79e87631ddc4084528e5df236bc7624472dcaa8480f1c60"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-kernel=${STAGING_INCDIR} \
               "
PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)} \
                 "

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
