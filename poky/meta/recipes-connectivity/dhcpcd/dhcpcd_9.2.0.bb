SECTION = "console/network"
SUMMARY = "dhcpcd - a DHCP client"
DESCRIPTION = "dhcpcd runs on your machine and silently configures your \
               computer to work on the attached networks without trouble \
               and mostly without configuration."

HOMEPAGE = "http://roy.marples.name/projects/dhcpcd/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9674cc803c5d71306941e6e8b5c002f2"

UPSTREAM_CHECK_URI = "https://roy.marples.name/downloads/dhcpcd/"

SRC_URI = "https://roy.marples.name/downloads/${BPN}/${BPN}-${PV}.tar.xz \
           file://0001-remove-INCLUDEDIR-to-prevent-build-issues.patch \
           file://dhcpcd.service \
           file://dhcpcd@.service \
           "

SRC_URI[sha256sum] = "fcb2d19672d445bbfd38678fdee4f556ef967a3ea6bd81092d10545df2cb9666"

inherit pkgconfig autotools-brokensep systemd useradd

SYSTEMD_SERVICE_${PN} = "dhcpcd.service"

PACKAGECONFIG ?= "udev ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[udev] = "--with-udev,--without-udev,udev,udev"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6"
# ntp conflicts with chrony
PACKAGECONFIG[ntp] = "--with-hook=ntp, , ,ntp"
PACKAGECONFIG[chrony] = "--with-hook=ntp, , ,chrony"
PACKAGECONFIG[ypbind] = "--with-eghook=yp, , ,ypbind-mt"

EXTRA_OECONF = "--enable-ipv4 \
                --dbdir=${localstatedir}/lib/${BPN} \
                --runstatedir=/run \
                --enable-privsep \
                --privsepuser=dhcpcd \
                --with-hooks \
                --with-eghooks \
               "

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system -d ${localstatedir}/lib/${BPN} -M -s /bin/false -U dhcpcd"

do_install_append () {
    # install systemd unit files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/dhcpcd*.service ${D}${systemd_unitdir}/system

    chmod 700 ${D}${localstatedir}/lib/${BPN}
    chown dhcpcd:dhcpcd ${D}${localstatedir}/lib/${BPN}
}

FILES_${PN}-dbg += "${libdir}/dhcpcd/dev/.debug"
