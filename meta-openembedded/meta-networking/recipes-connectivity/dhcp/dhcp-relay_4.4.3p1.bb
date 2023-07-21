SECTION = "console/network"
SUMMARY = "Internet Software Consortium DHCP Relay Agent"
DESCRIPTION = "A DHCP relay agent passes DHCP requests from one \
LAN to another, so that a DHCP server is not needed on every LAN."

HOMEPAGE = "http://www.isc.org/"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c463f4afde26d9eb60f14f50aeb85f8f"

DEPENDS = "openssl libcap zlib"

SRC_URI = "https://downloads.isc.org/isc/dhcp/4.4.3-P1/dhcp-4.4.3-P1.tar.gz \
           file://default-relay \
           file://init-relay \
           file://dhcrelay.service \
           file://0001-Makefile.am-only-build-dhcrelay.patch \
           file://0002-bind-Makefile.in-disable-backtrace.patch \
           file://0003-bind-Makefile.in-regenerate-configure.patch \
           "

SRC_URI[sha256sum] = "0ac416bb55997ca8632174fd10737fd61cdb8dba2752160a335775bc21dc73c7"

UPSTREAM_CHECK_URI = "http://ftp.isc.org/isc/dhcp/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.\d+\.(\d+?))/"

S = "${WORKDIR}/dhcp-4.4.3-P1"

inherit autotools-brokensep systemd pkgconfig

SYSTEMD_SERVICE:${PN} = "dhcrelay.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

CFLAGS += "-D_GNU_SOURCE -fcommon"
LDFLAGS:append = " -pthread"

BIND_EXTRA_CONFIG = "\
        --build=${BUILD_SYS} \
        --host=${HOST_SYS} \
        --target=${TARGET_SYS} \
"

EXTRA_OECONF = "--with-bind-extra-config="${BIND_EXTRA_CONFIG}" \
                --enable-paranoia \
                --disable-static \
                --enable-libtool \
                --with-randomdev=/dev/random \
               "

# Enable shared libs per dhcp README
do_configure:prepend () {
    cp configure.ac+lt configure.ac
}

do_install:append () {
    install -Dm 0644 ${WORKDIR}/default-relay ${D}${sysconfdir}/default/dhcp-relay

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/dhcrelay.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/dhcrelay.service
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/dhcrelay.service
    else
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/init-relay ${D}${sysconfdir}/init.d/dhcp-relay
    fi
}

PARALLEL_MAKE = ""

# dhcp-relay contains a bundled "bind", thus their dev packages conflict each other
RCONFLICTS:${PN}-dev = "bind-dev"
