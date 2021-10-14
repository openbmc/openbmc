SUMMARY = "Dynamic firewall daemon with a D-Bus interface"
HOMEPAGE = "https://firewalld.org/"
BUGTRACKER = "https://github.com/firewalld/firewalld/issues"
UPSTREAM_CHECK_URI = "https://github.com/firewalld/firewalld/releases"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
           file://firewalld.init \
"
SRC_URI[sha256sum] = "52c5e3d5b1e2efc0e86c22b2bc1f7fd80908cc2d8130157dc2a3517a59b0a760"

# glib-2.0-native is needed for GSETTINGS_RULES autoconf macro from gsettings.m4
DEPENDS = "intltool-native glib-2.0-native nftables"

inherit gettext autotools bash-completion pkgconfig python3native gsettings systemd update-rc.d

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--with-systemd-unitdir=${systemd_system_unitdir},--disable-systemd"
PACKAGECONFIG[docs] = "--with-xml-catalog=${STAGING_ETCDIR_NATIVE}/xml/catalog,--disable-docs,libxslt-native docbook-xsl-stylesheets-native"

PACKAGES += "${PN}-zsh-completion"

# iptables, ip6tables, ebtables, and ipset *should* be unnecessary
# when the nftables backend is available, because nftables supersedes all of them.
# However we still need iptables and ip6tables to be available otherwise any
# application relying on "direct passthrough" rules (such as docker) will break.
# /etc/sysconfig/firewalld is a Red Hat-ism, only referenced by
# the Red Hat-specific init script which we aren't using, so we disable that.
EXTRA_OECONF = "\
    --without-ipset \
    --with-iptables=${sbindir}/iptables \
    --with-iptables-restore=${sbindir}/iptables-restore \
    --with-ip6tables=${sbindir}/ip6tables \
    --with-ip6tables-restore=${sbindir}/ip6tables-restore \
    --without-ebtables \
    --without-ebtables-restore \
    --disable-sysconfig \
"

INITSCRIPT_NAME = "firewalld"
SYSTEMD_SERVICE:${PN} = "firewalld.service"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        :
    else
        # firewalld ships an init script but it contains Red Hat-isms, replace it with our own
        rm -rf ${D}${sysconfdir}/rc.d/
        install -d ${D}${sysconfdir}/init.d
        install -m0755 ${WORKDIR}/firewalld.init ${D}${sysconfdir}/init.d/firewalld
    fi

    # We ran ./configure with PYTHON pointed at the binary inside $STAGING_BINDIR_NATIVE
    # so now we need to fix up any references to point at the proper path in the image.
    # This hack is also in distutils.bbclass, but firewalld doesn't use distutils/setuptools.
    if [ ${PN} != "${BPN}-native" ]; then
        sed -i -e s:${STAGING_BINDIR_NATIVE}/python3-native/python3:${bindir}/python3:g \
            ${D}${bindir}/* ${D}${sbindir}/* ${D}${sysconfdir}/firewalld/*.xml
    fi
    sed -i -e s:${STAGING_BINDIR_NATIVE}:${bindir}:g \
        ${D}${bindir}/* ${D}${sbindir}/* ${D}${sysconfdir}/firewalld/*.xml

    # This file contains Red Hat-isms. Modules get loaded without it.
    rm -f ${D}${sysconfdir}/modprobe.d/firewalld-sysctls.conf
}

FILES:${PN} += "\
    ${PYTHON_SITEPACKAGES_DIR}/firewall \
    ${nonarch_libdir}/firewalld \
    ${datadir}/dbus-1 \
    ${datadir}/polkit-1 \
    ${datadir}/metainfo \
"
FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"

RDEPENDS:${PN} = "\
    nftables-python \
    iptables \
    python3-core \
    python3-io \
    python3-fcntl \
    python3-shell \
    python3-syslog \
    python3-xml \
    python3-dbus \
    python3-slip-dbus \
    python3-decorator \
    python3-pygobject \
    python3-json \
    python3-ctypes \
"
