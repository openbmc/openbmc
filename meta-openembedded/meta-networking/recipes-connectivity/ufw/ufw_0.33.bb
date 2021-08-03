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
           file://0006-check-requirements-get-error.patch \
           file://0007-use-conntrack-instead-of-state-module.patch \
           file://0008-support-.-setup.py-build-LP-819600.patch \
           file://0009-adjust-runtime-tests-to-use-daytime-port.patch \
           file://0010-empty-out-IPT_MODULES-and-update-documentation.patch \
           file://0011-tests-check-requirements--simplify-and-support-python-3.8.patch \
           file://Add-code-to-detect-openembedded-python-interpreter.patch \
"

UPSTREAM_CHECK_URI = "https://launchpad.net/ufw"

SRC_URI[md5sum] = "3747b453d76709e5a99da209fc0bb5f5"
SRC_URI[sha256sum] = "5f85a8084ad3539b547bec097286948233188c971f498890316dec170bdd1da8"

inherit setuptools3 features_check

do_install_append() {
    install -d ${D}${datadir}/${PN}/test
    cp -R --no-dereference --preserve=mode,links -v ${S}/* ${D}${datadir}/${PN}/test
}
PACKAGES =+ "${PN}-test"
RDEPENDS_${PN}-test += "bash"
FILES_${PN}-test += "${datadir}/${PN}/test"

# To test, install ufw-test package. You can enter /usr/share/ufw/test and run as root:
# PYTHONPATH=tests/testarea/lib/python ./run_tests.sh -s -i python3 root

RDEPENDS_${PN} = " \
                  iptables \
                  python3 \
                  python3-modules \
                 "

RRECOMMENDS_${PN} = " \
                     kernel-module-ipv6 \
                     kernel-module-ipt-reject \
                     kernel-module-iptable-mangle \
                     kernel-module-iptable-raw \
                     kernel-module-ip6table-raw \
                     kernel-module-ip6t-reject \
                     kernel-module-ip6t-rt \
                     kernel-module-ip6table-mangle \
                     kernel-module-nf-conntrack \
                     kernel-module-nf-log-common \
                     kernel-module-nf-conntrack-broadcast \
                     kernel-module-nf-conntrack-ftp \
                     kernel-module-nf-conntrack-netbios-ns \
                     kernel-module-nf-log-ipv4 \
                     kernel-module-nf-log-ipv6 \
                     kernel-module-nf-log-ipv4 \
                     kernel-module-nf-log-ipv6 \
                     kernel-module-nf-nat-ftp \
                     kernel-module-xt-addrtype \
                     kernel-module-xt-comment \
                     kernel-module-xt-conntrack \
                     kernel-module-xt-hashlimit \
                     kernel-module-xt-hl \
                     kernel-module-xt-multiport \
                     kernel-module-xt-ratetest \
                     kernel-module-xt-socket \
                     kernel-module-xt-tcpudp \
                     kernel-module-xt-limit \
                     kernel-module-xt-log \
                     kernel-module-xt-recent \
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
