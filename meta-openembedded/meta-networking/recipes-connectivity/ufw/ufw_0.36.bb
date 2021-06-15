
SUMMARY = "Uncomplicated Firewall"
DESCRIPTION = "UFW stands for Uncomplicated Firewall, and is program for \
managing a netfilter firewall. It provides a command line interface and aims \
to be uncomplicated and easy to use."
HOMEPAGE = "https://launchpad.net/ufw"
SECTION = "net"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = "https://launchpad.net/ufw/0.36/0.36/+download/ufw-0.36.tar.gz \
           file://0001-optimize-boot.patch \
           file://0002-add-an-option-to-specify-iptables-location.patch \
           file://0003-only-make-one-reference-to-env.patch \
           "

UPSTREAM_CHECK_URI = "https://launchpad.net/ufw"

SRC_URI[md5sum] = "6d8ab1506da21ae003f4628f93d05781"
SRC_URI[sha256sum] = "754b22ae5edff0273460ac9f57509c3938187e0cf4fb9692c6a02833fff33cfc"

inherit setuptools3 features_check systemd update-rc.d

RDEPENDS_${PN} = " \
                  iptables \
                  python3 \
                  python3-modules \
                 "

RRECOMMENDS_${PN} = " \
                     kernel-module-ipv6 \
                     kernel-module-nf-conntrack-ipv6 \
                     kernel-module-nf-log-common \
                     kernel-module-nf-log-ipv4 \
                     kernel-module-nf-log-ipv6 \
                     kernel-module-nf-addrtype \
                     kernel-module-nf-limit \
                     kernel-module-nf-log \
                     kernel-module-nf-recent \
"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/doc/systemd.example ${D}${systemd_unitdir}/system/ufw.service

    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${S}/doc/initscript.example ${D}${sysconfdir}/init.d/ufw
}

SYSTEMD_SERVICE_${PN} = "ufw.service"

INITSCRIPT_NAME = "ufw"
INITSCRIPT_PARAMS = "defaults"

# Certain items are explicitly put under /lib, not base_libdir when installed.
#
FILES_${PN} += " \
                ${sbindir}/* \
                ${datadir}/ufw/* \
                /lib/ufw/* \
                ${sysconfdir}/ufw/* \
                ${sysconfdir}/default/ufw \
"

REQUIRED_DISTRO_FEATURES = "ipv6"

DISTUTILS_BUILD_ARGS_append = " --iptables-dir /usr/sbin"
DISTUTILS_INSTALL_ARGS_append = " --iptables-dir /usr/sbin"
