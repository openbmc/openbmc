SUMMARY = "TPM2 Access Broker & Resource Manager"
DESCRIPTION = "This is a system daemon implementing the TPM2 access \
broker (TAB) & Resource Manager (RM) spec from the TCG. The daemon (tpm2-abrmd) \
is implemented using Glib and the GObject system. In this documentation and \
in the code we use `tpm2-abrmd` and `tabrmd` interchangeably. \
"
SECTION = "security/tpm"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

DEPENDS += "autoconf-archive dbus glib-2.0 pkgconfig tpm2.0-tss glib-2.0-native"

SRC_URI = "\
    git://github.com/01org/tpm2-abrmd.git \
    file://tpm2-abrmd-init.sh \
    file://tpm2-abrmd.default \
"
SRCREV = "59ce1008e5fa3bd5a143437b0f7390851fd25bd8"

S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd update-rc.d useradd

SYSTEMD_PACKAGES += "${PN}"
SYSTEMD_SERVICE_${PN} = "tpm2-abrmd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

INITSCRIPT_NAME = "${PN}"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 . stop 19 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "tss"
USERADD_PARAM_${PN} = "--system -M -d /var/lib/tpm -s /bin/false -g tss tss"

PACKAGECONFIG ?="udev"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd', '', d)}"

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, --with-systemdsystemunitdir=no"
PACKAGECONFIG[udev] = "--with-udevrulesdir=${sysconfdir}/udev/rules.d, --without-udevrulesdir"

do_install_append() {
    install -d "${D}${sysconfdir}/init.d"
    install -m 0755 "${WORKDIR}/tpm2-abrmd-init.sh" "${D}${sysconfdir}/init.d/tpm2-abrmd"

    install -d "${D}${sysconfdir}/default"
    install -m 0644 "${WORKDIR}/tpm2-abrmd.default" "${D}${sysconfdir}/default/tpm2-abrmd"
}

FILES_${PN} += "${libdir}/systemd/system-preset"

RDEPENDS_${PN} += "libgcc dbus-glib libtss2 libtctidevice libtctisocket"

BBCLASSEXTEND = "native"
