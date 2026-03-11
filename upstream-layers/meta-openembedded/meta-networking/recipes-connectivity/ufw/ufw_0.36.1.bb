
SUMMARY = "Uncomplicated Firewall"
DESCRIPTION = "UFW stands for Uncomplicated Firewall, and is program for \
managing a netfilter firewall. It provides a command line interface and aims \
to be uncomplicated and easy to use."
HOMEPAGE = "https://launchpad.net/ufw"
SECTION = "net"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = "https://launchpad.net/ufw/0.36/0.36.1/+download/ufw-0.36.1.tar.gz \
           file://0001-optimize-boot.patch \
           file://0002-add-an-option-to-specify-iptables-location.patch \
           file://0003-only-make-one-reference-to-env.patch \
           file://setuptools.patch \
           "
SRC_URI[sha256sum] = "1c57e78fbf2970f0cc9c56ea87a231e6d83d825e55b9e31e2c88b91b0ea03c8c"

UPSTREAM_CHECK_URI = "https://launchpad.net/ufw"

inherit setuptools3_legacy features_check systemd update-rc.d

RDEPENDS:${PN} = " \
                  iptables \
                  python3 \
                  python3-modules \
                 "

RRECOMMENDS:${PN} = " \
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


do_configure:prepend() {
    if ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','true','false',d)}; then
        sed -i -e 's|/lib|${nonarch_base_libdir}|' ${S}/setup.py
    fi
}

do_install:append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/doc/systemd.example ${D}${systemd_unitdir}/system/ufw.service

    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${S}/doc/initscript.example ${D}${sysconfdir}/init.d/ufw
}

SYSTEMD_SERVICE:${PN} = "ufw.service"

INITSCRIPT_NAME = "ufw"
INITSCRIPT_PARAMS = "defaults"

# Certain items are explicitly put under /lib, not base_libdir when installed.
#
FILES:${PN} += " \
                ${sbindir}/* \
                ${datadir}/ufw/* \
                ${nonarch_base_libdir}/ufw/* \
                ${sysconfdir}/ufw/* \
                ${sysconfdir}/default/ufw \
"

REQUIRED_DISTRO_FEATURES = "ipv6"

SETUPTOOLS_BUILD_ARGS:append = " --iptables-dir /usr/sbin"
SETUPTOOLS_INSTALL_ARGS:append = " --iptables-dir /usr/sbin"
