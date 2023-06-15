SECTION = "console/network"
SUMMARY = "dhcpcd - a DHCP client"
DESCRIPTION = "dhcpcd runs on your machine and silently configures your \
               computer to work on the attached networks without trouble \
               and mostly without configuration."

HOMEPAGE = "http://roy.marples.name/projects/dhcpcd/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d148485768fe85b9f1072b186a7e9b4d"

SRC_URI = "git://github.com/NetworkConfiguration/dhcpcd;protocol=https;branch=dhcpcd-9 \
           file://0001-remove-INCLUDEDIR-to-prevent-build-issues.patch \
           file://0001-20-resolv.conf-improve-the-sitation-of-working-with-.patch \
           file://0001-privsep-Allow-getrandom-sysctl-for-newer-glibc.patch \
           file://0002-privsep-Allow-newfstatat-syscall-as-well.patch \
           file://0001-privsep-linux-fix-SECCOMP_AUDIT_ARCH-missing-ppc64le.patch \
           file://dhcpcd.service \
           file://dhcpcd@.service \
           file://0001-dhcpcd.8-Fix-conflict-error-when-enable-multilib.patch \
           "

SRCREV = "3c458fc7fa4146029a1e4f9e98cd7e7adf03081a"
S = "${WORKDIR}/git"

inherit pkgconfig autotools-brokensep systemd useradd

SYSTEMD_SERVICE:${PN} = "dhcpcd.service"

PACKAGECONFIG ?= "udev ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[udev] = "--with-udev,--without-udev,udev,udev"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6"
# ntp conflicts with chrony
PACKAGECONFIG[ntp] = "--with-hook=ntp, , ,ntp"
PACKAGECONFIG[chrony] = "--with-hook=ntp, , ,chrony"
PACKAGECONFIG[ypbind] = "--with-eghook=yp, , ,ypbind-mt"

# add option to override DBDIR location
DBDIR ?= "${localstatedir}/lib/${BPN}"

EXTRA_OECONF = "--enable-ipv4 \
                --dbdir=${DBDIR} \
                --sbindir=${base_sbindir} \
                --runstatedir=/run \
                --enable-privsep \
                --privsepuser=dhcpcd \
                --with-hooks \
                --with-eghooks \
               "

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system -d ${DBDIR} -M -s /bin/false -U dhcpcd"

do_install:append () {
    # install systemd unit files
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/dhcpcd*.service ${D}${systemd_system_unitdir}

    chmod 700 ${D}${DBDIR}
    chown dhcpcd:dhcpcd ${D}${DBDIR}
}

FILES:${PN}-dbg += "${libdir}/dhcpcd/dev/.debug"
