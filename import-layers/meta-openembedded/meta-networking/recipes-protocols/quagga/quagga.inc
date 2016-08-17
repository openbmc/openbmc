SUMMARY = "BGP/OSPF/RIP routing daemon"
DESCRIPTION = "Quagga is a routing software suite, providing \
implementations of OSPFv2, OSPFv3, RIP v1 and v2, RIPv3 and BGPv4 for \
Unix platforms, particularly FreeBSD, Linux, Solaris and NetBSD. \
Quagga is a fork of GNU Zebra which was developed by Kunihiro \
Ishiguro. The Quagga tree aims to build a more involved community \
around Quagga than the current centralised model of GNU Zebra."
HOMEPAGE = "http://www.nongnu.org/quagga/"
SECTION = "net"
LICENSE = "GPL-2.0 & LGPL-2.0"
DEPENDS = "readline ncurses perl-native"
DEPENDS += "${@base_contains('DISTRO_FEATURES', 'snmp', 'net-snmp', '', d)}"
SNMP_CONF="${@base_contains('DISTRO_FEATURES', 'snmp', '--enable-snmp', '', d)}"

LIC_FILES_CHKSUM = "file://COPYING;md5=81bcece21748c91ba9992349a91ec11d \
                    file://COPYING.LIB;md5=01ef24401ded36cd8e5d18bfe947240c"

# the "ip" command from busybox is not sufficient (flush by protocol flushes all routes)
RDEPENDS_${PN} += "iproute2"

QUAGGASUBDIR = ""
# ${QUAGGASUBDIR} is deal with old versions. Set to "/attic" for old
# versions and leave it empty for recent versions.
SRC_URI = "${SAVANNAH_GNU_MIRROR}/quagga${QUAGGASUBDIR}/quagga-${PV}.tar.gz; \
           file://Zebra-sync-zebra-routing-table-with-the-kernel-one.patch \
           file://quagga.init \
           file://quagga.default \
           file://watchquagga.init \
           file://watchquagga.default \
           file://volatiles.03_quagga \
           file://quagga.pam \
           file://ripd-fix-two-bugs-after-received-SIGHUP.patch \
           file://quagga-Avoid-duplicate-connected-address.patch \
           file://babeld.service \
           file://bgpd.service \
           file://isisd.service \
           file://ospf6d.service \
           file://ospfd.service \
           file://ripd.service \
           file://ripngd.service \
           file://zebra.service \
"

PACKAGECONFIG ??= "${@base_contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[cap] = "--enable-capabilities,--disable-capabilities,libcap"
PACKAGECONFIG[pam] = "--with-libpam, --without-libpam, libpam"

inherit autotools update-rc.d useradd systemd

SYSTEMD_PACKAGES = "${PN} ${PN}-babeld ${PN}-bgpd ${PN}-isisd ${PN}-ospf6d ${PN}-ospfd ${PN}-ripd ${PN}-ripngd"
SYSTEMD_SERVICE_${PN}-babeld = "babeld.service"
SYSTEMD_SERVICE_${PN}-bgpd = "bgpd.service"
SYSTEMD_SERVICE_${PN}-isisd = "isisd.service"
SYSTEMD_SERVICE_${PN}-ospf6d = "ospf6d.service"
SYSTEMD_SERVICE_${PN}-ospfd = "ospfd.service"
SYSTEMD_SERVICE_${PN}-ripd = "ripd.service"
SYSTEMD_SERVICE_${PN}-ripngd = "ripngd.service"
SYSTEMD_SERVICE_${PN} = "zebra.service"

EXTRA_OECONF = "--sysconfdir=${sysconfdir}/quagga \
                --localstatedir=${localstatedir}/run/quagga \
                --enable-exampledir=${docdir}/quagga/examples/ \
                --enable-vtysh \
                --enable-isisd \
                ${@base_contains('DISTRO_FEATURES', 'sysvinit', '--enable-watchquagga', '--disable-watchquagga', d)} \
                --enable-ospf-te \
                --enable-opaque-lsa \
                --enable-ospfclient=yes \
                --enable-multipath=64 \
                --enable-user=quagga \
                --enable-group=quagga \
                --enable-vty-group=quaggavty \
                --enable-configfile-mask=0640 \
                --enable-logfile-mask=0640 \
                --enable-rtadv \
                --enable-linux24-tcp-md5 \
                ap_cv_cc_pie=no \
                ${SNMP_CONF}"

do_install () {
    # Install init script and default settings
    install -m 0755 -d ${D}${sysconfdir}/default ${D}${sysconfdir}/init.d \
    ${D}${sysconfdir}/quagga ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/quagga.default ${D}${sysconfdir}/default/quagga
    install -m 0644 ${WORKDIR}/watchquagga.default ${D}${sysconfdir}/default/watchquagga
    install -m 0755 ${WORKDIR}/quagga.init ${D}${sysconfdir}/init.d/quagga
    install -m 0755 ${WORKDIR}/watchquagga.init ${D}${sysconfdir}/init.d/watchquagga
    install -m 0644 ${WORKDIR}/volatiles.03_quagga  ${D}${sysconfdir}/default/volatiles/volatiles.03_quagga

    # Install sample configurations for the daemons
    for f in bgpd vtysh babeld isisd ospfd ripngd zebra ripd ospf6d; do
        install -m 0640 ${S}/$f/$f.conf.sample ${D}${sysconfdir}/quagga/$f.conf.sample
    done

    for f in bgpd vtysh babeld isisd ospfd ripngd zebra ripd ospf6d; do
	touch ${D}${sysconfdir}/quagga/$f.conf
    done
    chown quagga:quaggavty ${D}${sysconfdir}/quagga
    chown quagga:quagga ${D}${sysconfdir}/quagga/*.conf
    chmod 750 ${D}${sysconfdir}/quagga
    chmod 640 ${D}${sysconfdir}/quagga/*.conf

    # Install quagga
    oe_runmake install DESTDIR=${D} prefix=${prefix} \
            sbindir=${sbindir} \
            sysconfdir=${sysconfdir}/quagga \
            localstatedir=${localstatedir}/run/quagga

    # Fix hardcoded paths
    sed -i 's!/usr/sbin/!${sbindir}/!g' ${D}${sysconfdir}/init.d/*
    sed -i 's!/usr/bin/!${bindir}/!g' ${D}${sysconfdir}/init.d/quagga
    sed -i 's!/etc/!${sysconfdir}/!g' ${D}${sysconfdir}/init.d/* ${D}${sysconfdir}/default/watchquagga
    sed -i 's!/var/!${localstatedir}/!g' ${D}${sysconfdir}/init.d/* ${D}${sysconfdir}/default/volatiles/volatiles.03_quagga
    sed -i 's!^PATH=.*!PATH=${base_sbindir}:${sbindir}:${base_bindir}:${bindir}!' ${D}${sysconfdir}/init.d/*

    # Remove generated info dir file, it doesn't belong in the generated
    # package.
    rm -f ${D}${infodir}/dir

    # For PAM
    for feature in ${DISTRO_FEATURES}; do
        if [ "$feature" = "pam" ]; then
            install -D -m 644 ${WORKDIR}/quagga.pam ${D}/${sysconfdir}/pam.d/quagga
            break
        fi
    done

    if ${@base_contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /var/run/quagga 0755 quagga quagga -" \
        > ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi

    # Remove sysinit script if sysvinit is not in DISTRO_FEATURES
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'false', 'true', d)}; then
        rm -rf ${D}${sysconfdir}/init.d/
        rm -f ${D}${sysconfdir}/default/watchquagga
    fi

    install -d ${D}${systemd_unitdir}/system
    for i in babeld bgpd isisd ospf6d ospfd ripd ripngd zebra; do
        install -m 0644 ${WORKDIR}/$i.service ${D}${systemd_unitdir}/system
    done
    sed -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@base_bindir@,${base_bindir},g' \
        -e 's,@localstatedir@,${localstatedir},g' \
        -i ${D}${systemd_unitdir}/system/*.service
}

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-systemctl-native', '', d)}"
pkg_postinst_${PN} () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd sysvinit', 'true', 'false', d)}; then
        if [ -n "$D" ]; then
            OPTS="--root=$D"
        fi
        systemctl $OPTS mask quagga.service
    fi
}

# Split into a main package and separate per-protocol packages
PACKAGE_BEFORE_PN = "${PN}-ospfd ${PN}-ospf6d ${PN}-babeld ${PN}-bgpd \
                     ${PN}-ripd ${PN}-ripngd ${PN}-isisd \
                     ${PN}-ospfclient ${@base_contains('DISTRO_FEATURES', 'sysvinit', '${PN}-watchquagga', '', d)}"

RDEPENDS_${PN} += "${PN}-babeld ${PN}-bgpd ${PN}-isisd ${PN}-ospf6d ${PN}-ospfd ${PN}-ripd ${PN}-ripngd"

FILES_${PN}-ospfd       = "${sbindir}/ospfd ${libdir}/libospf.so.*"
FILES_${PN}-ospf6d      = "${sbindir}/ospf6d"
FILES_${PN}-babeld      = "${sbindir}/babeld"
FILES_${PN}-bgpd        = "${sbindir}/bgpd"
FILES_${PN}-ripd        = "${sbindir}/ripd"
FILES_${PN}-ripngd      = "${sbindir}/ripngd"
FILES_${PN}-isisd       = "${sbindir}/isisd"
FILES_${PN}-ospfclient  = "${sbindir}/ospfclient ${libdir}/libospfapiclient.so.*"
FILES_${PN}-watchquagga = "${sbindir}/watchquagga ${sysconfdir}/default/watchquagga \
                           ${sysconfdir}/init.d/watchquagga"

# Indicate that the default files are configuration files
CONFFILES_${PN} = "${sysconfdir}/default/quagga \
                   ${sysconfdir}/quagga/bgpd.conf \
                   ${sysconfdir}/quagga/vtysh.conf \
                   ${sysconfdir}/quagga/babeld.conf \
                   ${sysconfdir}/quagga/isisd.conf \
                   ${sysconfdir}/quagga/ospfd.conf \
                   ${sysconfdir}/quagga/ripngd.conf \
                   ${sysconfdir}/quagga/zebra.conf \
                   ${sysconfdir}/quagga/ripd.conf \
                   ${sysconfdir}/quagga/ospf6d.conf \
                  "
CONFFILES_${PN}-watchquagga = "${sysconfdir}/default/watchquagga"

# Stop the names being rewritten due to the internal shared libraries
DEBIAN_NOAUTONAME_${PN}-ospfd = "1"
DEBIAN_NOAUTONAME_${PN}-ospfclient = "1"

# the "ip" command from busybox is not sufficient (flush by protocol flushes all routes)
RDEPENDS_${PN}          += "iproute2"

# Main init script starts all deamons
# Seperate init script for watchquagga
INITSCRIPT_PACKAGES                     = "${PN} ${PN}-watchquagga"
INITSCRIPT_NAME_${PN}                   = "quagga"
INITSCRIPT_PARAMS_${PN}                 = "defaults 15 85"
INITSCRIPT_NAME_${PN}-watchquagga       = "watchquagga"
INITSCRIPT_PARAMS_${PN}-watchquagga     = "defaults 90 10"

# Add quagga's user and group
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system quagga ; --system quaggavty"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/run/quagga/ -M -g quagga --shell /bin/false quagga"

pkg_postinst_${PN} () {
    if [ -z "$D" ] && [ -e /etc/init.d/populate-volatile.sh ] ; then
        ${sysconfdir}/init.d/populate-volatile.sh update
    fi
}

# Stop apps before uninstall
pkg_prerm_${PN} () {
    ${sysconfdir}/init.d/quagga stop
}

pkg_prerm_${PN}-ospfd () {
    ${sysconfdir}/init.d/quagga stop ospfd
}

pkg_prerm_${PN}-ospf6d () {
    ${sysconfdir}/init.d/quagga stop ospf6d
}

pkg_prerm_${PN}-babeld () {
    ${sysconfdir}/init.d/quagga stop babeld
}

pkg_prerm_${PN}-bgpd () {
    ${sysconfdir}/init.d/quagga stop bgpd
}

pkg_prerm_${PN}-ripd () {
    ${sysconfdir}/init.d/quagga stop ripd
}

pkg_prerm_${PN}-ripngd () {
    ${sysconfdir}/init.d/quagga stop ripngd
}

pkg_prerm_${PN}-isisd () {
    ${sysconfdir}/init.d/quagga stop isisd
}
