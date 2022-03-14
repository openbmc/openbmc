SECTION = "console/network"
SUMMARY = "Internet Software Consortium DHCP Relay Agent"
DESCRIPTION = "A DHCP relay agent passes DHCP requests from one \
LAN to another, so that a DHCP server is not needed on every LAN."

HOMEPAGE = "http://www.isc.org/"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;md5=004a4db50a1e20972e924a8618747c01"

DEPENDS = "openssl libcap zlib"

SRC_URI = "https://ftp.isc.org/isc/dhcp/4.4.2-P1/dhcp-4.4.2-P1.tar.gz \
           https://ftp.isc.org/isc/bind9/9.11.32/bind-9.11.32.tar.gz;name=bind;unpack=0 \
           file://default-relay \
           file://init-relay \
           file://dhcrelay.service \
           file://0001-Makefile.am-only-build-dhcrelay.patch \
           file://0002-bind-version-update-to-latest-version.patch \
           file://0003-bind-Makefile.in-disable-backtrace.patch \
           "

SRC_URI[md5sum] = "3089a1ebd20a802ec0870ae337d43907"
SRC_URI[sha256sum] = "b05e04337539545a8faa0d6ac518defc61a07e5aec66a857f455e7f218c85a1a"
SRC_URI[bind.md5sum] = "0d029dd06ca60c6739c3189c999ef757"
SRC_URI[bind.sha256sum] = "cbf8cb4b74dd1452d97c3a2a8c625ea346df8516b4b3508ef07443121a591342"

UPSTREAM_CHECK_URI = "http://ftp.isc.org/isc/dhcp/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.\d+\.(\d+?))/"

S = "${WORKDIR}/dhcp-4.4.2-P1"

inherit autotools-brokensep systemd

SYSTEMD_SERVICE:${PN} = "dhcrelay.service"
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

CFLAGS += "-D_GNU_SOURCE -fcommon"
LDFLAGS:append = " -pthread"

EXTRA_OECONF = "--enable-paranoia \
                --disable-static \
                --enable-libtool \
                --with-randomdev=/dev/random \
               "

# Enable shared libs per dhcp README
do_configure:prepend () {
    cp configure.ac+lt configure.ac
    cp ${WORKDIR}/bind-9.11.32.tar.gz ${S}/bind/bind.tar.gz
}

do_compile:prepend() {
    # Need to unpack this now instead of earlier as do_configure will delete the configure script
    rm -rf ${S}/bind/bind-9.11.32/
    tar xf ${S}/bind/bind.tar.gz -C ${S}/bind
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/bind/bind-9.11.32/
    install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/bind/bind-9.11.32/
    cp -fpR ${S}/m4/*.m4 ${S}/bind/bind-9.11.32/libtool.m4/
    rm -rf ${S}/bind/bind-9.11.32/libtool
    install -m 0755 ${S}/libtool ${S}/bind/bind-9.11.32/
}

do_install:append () {
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
