SUMMARY = "TCP / IP networking and traffic control utilities"
DESCRIPTION = "Iproute2 is a collection of utilities for controlling \
TCP / IP networking and traffic control in Linux.  Of the utilities ip \
and tc are the most important.  ip controls IPv4 and IPv6 \
configuration and tc stands for traffic control."
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/iproute2"
SECTION = "base"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    "

DEPENDS = "flex-native bison-native iptables libcap"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/net/${BPN}/${BP}.tar.xz \
           file://0001-include-libnetlink.h-add-missing-include-for-htobe64.patch \
           "

SRC_URI:append:libc-musl = "\
           file://0002-bridge-mst-fix-a-musl-build-issue.patch \
           file://0003-bridge-mst-fix-a-further-musl-build-issue.patch \ 
           "

SRC_URI[sha256sum] = "1f795398a04aeaacd06a8f6ace2cfd913c33fa5953ca99daae83bb5c534611c3"

inherit update-alternatives bash-completion pkgconfig

PACKAGECONFIG ??= "tipc elf devlink"
PACKAGECONFIG[tipc] = ",,libmnl,"
PACKAGECONFIG[elf] = ",,elfutils,"
PACKAGECONFIG[devlink] = ",,libmnl,"
PACKAGECONFIG[rdma] = ",,libmnl,"
PACKAGECONFIG[selinux] = ",,libselinux"

IPROUTE2_MAKE_SUBDIRS = "lib tc ip bridge misc genl ${@bb.utils.filter('PACKAGECONFIG', 'devlink tipc rdma', d)}"

# This is needed with GCC-14 and musl
CFLAGS += "-Wno-error=incompatible-pointer-types"
# CFLAGS are computed in Makefile and reference CCOPTS
#
EXTRA_OEMAKE = "\
    CC='${CC}' \
    KERNEL_INCLUDE=${STAGING_INCDIR} \
    DOCDIR=${docdir}/iproute2 \
    SUBDIRS='${IPROUTE2_MAKE_SUBDIRS}' \
    SBINDIR='${base_sbindir}' \
    CONF_USR_DIR='${libdir}/iproute2' \
    LIBDIR='${libdir}' \
    CCOPTS='${CFLAGS}' \
"

do_configure:append () {
    sh configure ${STAGING_INCDIR}
    # Explicitly disable ATM support
    sed -i -e '/TC_CONFIG_ATM/d' config.mk
}

do_install () {
    oe_runmake DESTDIR=${D} install
    mv ${D}${base_sbindir}/ip ${D}${base_sbindir}/ip.iproute2
    install -d ${D}${datadir}
    mv ${D}/share/* ${D}${datadir}/ || true
    rm ${D}/share -rf || true

    # Remove support fot ipt and xt in tc. So tc library directory is not needed.
    rm ${D}${libdir}/tc -rf
}

# The .so files in iproute2-tc are modules, not traditional libraries
INSANE_SKIP:${PN}-tc = "dev-so"

IPROUTE2_PACKAGES =+ "\
    ${PN}-bridge \
    ${PN}-devlink \
    ${PN}-genl \
    ${PN}-ifstat \
    ${PN}-ip \
    ${PN}-lnstat \
    ${PN}-nstat \
    ${PN}-routel \
    ${PN}-rtacct \
    ${PN}-ss \
    ${PN}-tc \
    ${PN}-tipc \
    ${PN}-rdma \
"

PACKAGE_BEFORE_PN = "${IPROUTE2_PACKAGES}"
RDEPENDS:${PN} += "${PN}-ip"

FILES:${PN}-tc = "${base_sbindir}/tc* \
                  ${libdir}/tc/*.so"
FILES:${PN}-lnstat = "${base_sbindir}/lnstat \
                      ${base_sbindir}/ctstat \
                      ${base_sbindir}/rtstat"
FILES:${PN}-ifstat = "${base_sbindir}/ifstat"
FILES:${PN}-ip = "${base_sbindir}/ip.* ${libdir}/iproute2"
FILES:${PN}-genl = "${base_sbindir}/genl"
FILES:${PN}-rtacct = "${base_sbindir}/rtacct"
FILES:${PN}-nstat = "${base_sbindir}/nstat"
FILES:${PN}-ss = "${base_sbindir}/ss"
FILES:${PN}-tipc = "${base_sbindir}/tipc"
FILES:${PN}-devlink = "${base_sbindir}/devlink"
FILES:${PN}-rdma = "${base_sbindir}/rdma"
FILES:${PN}-routel = "${base_sbindir}/routel"
FILES:${PN}-bridge = "${base_sbindir}/bridge"

RDEPENDS:${PN}-routel = "python3-core"

ALTERNATIVE:${PN}-ip = "ip"
ALTERNATIVE_TARGET[ip] = "${base_sbindir}/ip.${BPN}"
ALTERNATIVE_LINK_NAME[ip] = "${base_sbindir}/ip"
ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN}-tc = "tc"
ALTERNATIVE_LINK_NAME[tc] = "${base_sbindir}/tc"
ALTERNATIVE_PRIORITY_${PN}-tc = "100"
