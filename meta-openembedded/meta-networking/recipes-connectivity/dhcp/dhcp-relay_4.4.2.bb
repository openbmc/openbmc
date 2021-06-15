SECTION = "console/network"
SUMMARY = "Internet Software Consortium DHCP Relay Agent"
DESCRIPTION = "A DHCP relay agent passes DHCP requests from one \
LAN to another, so that a DHCP server is not needed on every LAN."

HOMEPAGE = "http://www.isc.org/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;md5=004a4db50a1e20972e924a8618747c01"

DEPENDS = "openssl libcap zlib"

SRC_URI = "https://ftp.isc.org/isc/dhcp/${PV}/dhcp-${PV}.tar.gz \
           file://default-relay \
           file://init-relay \
           file://dhcrelay.service \
           file://0001-Makefile.am-only-build-dhcrelay.patch \
           "

SRC_URI[md5sum] = "2afdaf8498dc1edaf3012efdd589b3e1"
SRC_URI[sha256sum] = "1a7ccd64a16e5e68f7b5e0f527fd07240a2892ea53fe245620f4f5f607004521"

UPSTREAM_CHECK_URI = "http://ftp.isc.org/isc/dhcp/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.\d+\.(\d+?))/"

S = "${WORKDIR}/dhcp-${PV}"

inherit autotools-brokensep systemd

SYSTEMD_SERVICE_${PN} = "dhcrelay.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

CFLAGS += "-D_GNU_SOURCE -fcommon"
LDFLAGS_append = " -pthread"

EXTRA_OECONF = "--enable-paranoia \
                --disable-static \
                --enable-libtool \
                --with-randomdev=/dev/random \
               "
EXTRA_OEMAKE += "LIBTOOL='${S}/${HOST_SYS}-libtool'"

# Enable shared libs per dhcp README
do_configure_prepend () {
    cp configure.ac+lt configure.ac
}
do_compile_prepend() {
    rm -rf ${S}/bind/bind-9.11.14/
    tar xf ${S}/bind/bind.tar.gz -C ${S}/bind
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/bind/bind-9.11.14/
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/bind/bind-9.11.14/
    cp -fpR ${S}/m4/*.m4 ${S}/bind/bind-9.11.14/libtool.m4/
    rm -rf ${S}/bind/bind-9.11.14/libtool
    install -m 0755 ${S}/${HOST_SYS}-libtool ${S}/bind/bind-9.11.14/
}

do_install_append () {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/default-relay ${D}${sysconfdir}/default/dhcp-relay

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
