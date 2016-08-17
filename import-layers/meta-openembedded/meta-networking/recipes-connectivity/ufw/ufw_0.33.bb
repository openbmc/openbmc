SUMMARY = "Uncomplicated Firewall"
DESCRIPTION = "UFW stands for Uncomplicated Firewall, and is program for \
managing a netfilter firewall. It provides a command line interface and aims \
to be uncomplicated and easy to use."
HOMEPAGE = "https://launchpad.net/ufw"
SECTION = "net"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = " \
           https://launchpad.net/ufw/0.33/0.33/+download/ufw-0.33.tar.gz \
           file://setup-add-an-option-to-specify-iptables-location.patch \
           file://setup-only-make-one-reference-to-env.patch \
           file://0001-optimize-boot.patch \
           file://0002-lp1044361.patch \
           file://0003-fix-typeerror-on-error.patch \
           file://0004-lp1039729.patch \
           file://0005-lp1191197.patch \
"
SRC_URI[md5sum] = "3747b453d76709e5a99da209fc0bb5f5"
SRC_URI[sha256sum] = "5f85a8084ad3539b547bec097286948233188c971f498890316dec170bdd1da8"

inherit setuptools distro_features_check

RDEPENDS_${PN} = " \
                  iptables \
                  python \
                  python-modules \
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
