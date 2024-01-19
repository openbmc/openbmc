SUMMARY = "BGP/OSPF/RIP routing daemon"
DESCRIPTION = "FRRouting is a free and open source Internet routing protocol suite for Linux \
and Unix platforms. It implements BGP, OSPF, RIP, IS-IS, PIM, LDP, BFD, Babel, PBR, OpenFabric \
and VRRP, with alpha support for EIGRP and NHRP."
HOMEPAGE = "https://frrouting.org/"
SECTION = "net"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://doc/licenses/GPL-2.0;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://doc/licenses/LGPL-2.1;md5=4fbd65380cdd255951079008b364516c"

PR = "r1"

SRC_URI = "git://github.com/FRRouting/frr.git;protocol=https;branch=stable/9.1 \
           file://frr.pam \
           "

SRCREV = "312faf8008bb4f3b9e84b8e2758cd2cbdf5742b5"

UPSTREAM_CHECK_GITTAGREGEX = "frr-(?P<pver>\d+(\.\d+)+)$"

CVE_PRODUCT = "frrouting"

S = "${WORKDIR}/git"

inherit autotools-brokensep python3native pkgconfig useradd systemd

DEPENDS:class-native = "bison-native elfutils-native"
DEPENDS:class-target = "bison-native json-c readline c-ares libyang frr-native protobuf-c-native protobuf-c"

RDEPENDS:${PN}:class-target = "iproute2 python3-core bash"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG:class-native = ""

PACKAGECONFIG[fpm] = "--enable-fpm,--disable-fpm"
PACKAGECONFIG[pam] = "--with-libpam,--without-libpam,libpam"
PACKAGECONFIG[grpc] = "--enable-grpc,--disable-grpc,grpc-native grpc"
PACKAGECONFIG[snmp] = "--enable-snmp,--disable-snmp,net-snmp"
PACKAGECONFIG[zeromq] = "--enable-zeromq,--disable-zeromq,zeromq"
PACKAGECONFIG[protobuf] = "--enable-protobuf,--disable-protobuf,protobuf-c-native protobuf-c"
PACKAGECONFIG[capabilities] = "--enable-capabilities,--disable-capabilities,libcap"
PACKAGECONFIG[cumulus] = "--enable-cumulus,--disable-cumulus"
PACKAGECONFIG[datacenter] = "--enable-datacenter,--disable-datacenter"
PACKAGECONFIG[ospfclient] = "--enable-ospfapi --enable-ospfclient,--disable-ospfapi --disable-ospfclient"

EXTRA_OECONF:class-native = "--enable-clippy-only"

EXTRA_OECONF:class-target = "--sbindir=${libexecdir}/frr \
                             --sysconfdir=${sysconfdir}/frr \
                             --localstatedir=${localstatedir}/run/frr \
                             --enable-vtysh \
                             --enable-multipath=64 \
                             --enable-user=frr \
                             --enable-group=frr \
                             --enable-vty-group=frrvty \
                             --enable-configfile-mask=0640 \
                             --enable-logfile-mask=0640 \
                             --disable-doc \
                             --with-clippy=${RECIPE_SYSROOT_NATIVE}/usr/lib/clippy \
                            "

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl'"

# https://github.com/FRRouting/frr/issues/14469
DEBUG_PREFIX_MAP:remove = "-fcanon-prefix-map"

LDFLAGS:append:mips = " -latomic"
LDFLAGS:append:mipsel = " -latomic"
LDFLAGS:append:powerpc = " -latomic"
LDFLAGS:append:riscv32 = " -latomic"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "frr.service"
SYSTEMD_AUTO_ENABLE = "disable"

inherit update-alternatives multilib_script multilib_header

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = " ietf-interfaces "
ALTERNATIVE_LINK_NAME[ietf-interfaces] = "${datadir}/yang/ietf-interfaces.yang"
do_compile:prepend () {
   sed -i -e 's#${RECIPE_SYSROOT_NATIVE}##g' \
          -e 's#${RECIPE_SYSROOT}##g' ${S}/lib/version.h
}

do_compile:class-native () {
    oe_runmake clippy-only
}

do_install:class-native () {
    install -d ${D}${libdir}
    install -m 755 ${S}/lib/clippy ${D}${libdir}/clippy
}

do_install:append:class-target () {
    install -m 0755 -d ${D}${sysconfdir}/frr
    install -m 0755 -d ${D}${libexecdir}/frr
    install -m 0640 ${S}/tools/etc/frr/* ${D}${sysconfdir}/frr/
    chown frr:frrvty ${D}${sysconfdir}/frr
    chown frr:frr ${D}${sysconfdir}/frr/*
    chown frr:frrvty ${D}${sysconfdir}/frr/vtysh.conf
    chmod 640 ${D}${sysconfdir}/frr/*

    if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
        install -d ${D}/${sysconfdir}/pam.d
        install -m 644 ${WORKDIR}/frr.pam ${D}/${sysconfdir}/pam.d/frr
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${B}/tools/frrinit.sh ${D}${sysconfdir}/init.d/frr

        install -d ${D}${sysconfdir}/default/volatiles
        echo "d frr frr 0755 ${localstatedir}/run/frr none" \
            > ${D}${sysconfdir}/default/volatiles/99_frr
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${B}/tools/frr*.service ${D}${systemd_system_unitdir}

        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d /run/frr 0755 frr frr -" \
            > ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf
    fi
    oe_multilib_header frr/version.h
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system frr ; --system frrvty"
USERADD_PARAM:${PN} = "--system --home ${localstatedir}/run/frr/ -M -g frr -G frrvty --shell /bin/false frr"

FILES:${PN} += "${datadir}/yang"

BBCLASSEXTEND = "native"
