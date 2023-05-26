DESCRIPTION = "LIRC is a package that allows you to decode and send infra-red signals of many commonly used remote controls."
DESCRIPTION:append:lirc = " This package contains the lirc daemon, libraries and tools."
DESCRIPTION:append:lirc-exec = " This package contains a daemon that runs programs on IR signals."
DESCRIPTION:append:lirc-remotes = " This package contains some config files for remotes."
DESCRIPTION:append:lirc-nslu2example = " This package contains a working config for RC5 remotes and a modified NSLU2."
HOMEPAGE = "http://www.lirc.org"
SECTION = "console/network"
LICENSE = "GPL-2.0-only"
DEPENDS = "libxslt-native alsa-lib libftdi libusb1 libusb-compat jack portaudio-v19 python3-pyyaml python3-setuptools-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://prdownloads.sourceforge.net/lirc/lirc-${PV}.tar.bz2 \
           file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch \
           file://fix_build_errors.patch \
           file://0001-mplay-Fix-build-with-musl.patch \
           file://lircd.service \
           file://lircd.init \
           file://lircexec.init \
           file://lircd.conf \
           file://lirc_options.conf \
           file://lirc.tmpfiles \
           file://0001-Makefile.am-do-not-clobber-PYTHONPATH-from-build-env.patch \
           file://0001-Unbolt-ubuntu-hack.patch \
           "
SRC_URI[sha256sum] = "3d44ec8274881cf262f160805641f0827ffcc20ade0d85e7e6f3b90e0d3d222a"

SYSTEMD_PACKAGES = "lirc lirc-exec"
SYSTEMD_SERVICE:${PN} = "lircd.service lircmd.service lircd-setup.service lircd-uinput.service"
SYSTEMD_SERVICE:${PN}-exec = "irexec.service"
SYSTEMD_AUTO_ENABLE:lirc = "enable"
SYSTEMD_AUTO_ENABLE:lirc-exec = "enable"

inherit autotools pkgconfig systemd python3native setuptools3-base

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir,systemd"
PACKAGECONFIG[x11] = "--with-x,--with-x=no,libx11,"

PACKAGECONFIG ?= " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' x11', '', d)} \
"
CACHED_CONFIGUREVARS = "HAVE_WORKING_POLL=yes SH_PATH=/bin/sh"

#EXTRA_OEMAKE = 'SUBDIRS="lib daemons tools"'

# Ensure python-pkg/VERSION exists
do_configure:append() {
    cp ${S}/VERSION ${S}/python-pkg/
}

# Create PYTHON_TARBALL which LIRC needs for install-nodist_pkgdataDATA
do_install:prepend() {
    rm -rf ${S}/python-pkg/dist/
    mkdir ${S}/python-pkg/dist/
    tar --exclude='${S}/python-pkg/*' -czf ${S}/python-pkg/dist/${BP}.tar.gz ${S}
}

# In code, path to python is a variable that is replaced with path to native version of it
# during the configure stage, e.g ../recipe-sysroot-native/usr/bin/python3-native/python3.
# Replace it with #!/usr/bin/env python3
do_install:append() {
    sed -i '1c#!/usr/bin/env python3' ${D}${bindir}/lirc-setup \
                                      ${D}${PYTHON_SITEPACKAGES_DIR}/lirc-setup/lirc-setup \
                                      ${D}${bindir}/irtext2udp \
                                      ${D}${bindir}/lirc-init-db \
                                      ${D}${bindir}/irdb-get \
                                      ${D}${bindir}/pronto2lirc \
                                      ${D}${sbindir}/lircd-setup

    install -m 0755 -d ${D}${sysconfdir}
    install -m 0755 -d ${D}${sysconfdir}/lirc
    install -m 0644 ${WORKDIR}/lircd.conf ${D}${sysconfdir}/lirc/
    install -m 0644 ${WORKDIR}/lirc_options.conf ${D}${sysconfdir}/lirc/
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0755 -d ${D}${systemd_unitdir}/system ${D}${libdir}/tmpfiles.d
        install -m 0644 ${WORKDIR}/lircd.service ${D}${systemd_unitdir}/system/
        install -m 0755 ${WORKDIR}/lircexec.init ${D}${systemd_unitdir}/system/
        install -m 0644 ${WORKDIR}/lirc.tmpfiles ${D}${libdir}/tmpfiles.d/lirc.conf
    else
        rm -rf ${D}/lib
    fi
    rm -rf ${D}${libdir}/lirc/plugins/*.la
    rmdir ${D}/var/run/lirc ${D}/var/run
    chown -R root:root ${D}${datadir}/lirc/contrib
}

PACKAGES =+ "${PN}-contrib ${PN}-exec ${PN}-plugins ${PN}-python"

RDEPENDS:${PN} = "bash python3"
RDEPENDS:${PN}-exec = "${PN}"
RDEPENDS:${PN}-python = "python3-shell python3-pyyaml python3-datetime python3-netclient python3-stringold"

RRECOMMENDS:${PN} = "${PN}-exec ${PN}-plugins"

FILES:${PN}-plugins = "${libdir}/lirc/plugins/*.so ${datadir}/lirc/configs"
FILES:${PN}-contrib = "${datadir}/lirc/contrib"
FILES:${PN}-exec = "${bindir}/irexec ${sysconfdir}/lircexec ${systemd_unitdir}/system/irexec.service"
FILES:${PN} += "${systemd_unitdir}/system/lircexec.init"
FILES:${PN} += "${systemd_unitdir}/system/lircd.service"
FILES:${PN} += "${systemd_unitdir}/system/lircd.socket"
FILES:${PN} += "${libdir}/tmpfiles.d/lirc.conf"
FILES:${PN}-dbg += "${libdir}/lirc/plugins/.debug"
FILES:${PN}-python += "${bindir}/irdb-get ${bindir}/irtext2udp ${bindir}/lircd-setup ${bindir}/pronto2lirc ${libdir}/python*/site-packages"

INITSCRIPT_PACKAGES = "lirc lirc-exec"
INITSCRIPT_NAME:lirc-exec = "lircexec"
INITSCRIPT_PARAMS:lirc-exec = "defaults 21"

# this is for distributions that don't use udev
pkg_postinst:${PN}:append() {
    if [ ! -c $D/dev/lirc -a ! -f /sbin/udevd ]; then mknod $D/dev/lirc c 61 0; fi
}

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
