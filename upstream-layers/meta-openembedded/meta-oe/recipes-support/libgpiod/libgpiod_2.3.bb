require libgpiod.inc

inherit meson systemd update-rc.d useradd gobject-introspection

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & CC-BY-SA-4.0"
LIC_FILES_CHKSUM = " \
    file://LICENSES/GPL-2.0-or-later.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://LICENSES/LGPL-2.1-or-later.txt;md5=4b54a1fd55a448865a0b32d41598759d \
    file://LICENSES/CC-BY-SA-4.0.txt;md5=fba3b94d88bfb9b81369b869a1e9a20f \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-2.x:"

SRC_URI += " \
    file://gpio-manager.init \
"

SRC_URI[sha256sum] = "cb71db463aec2604ac520c95bf04eff1839d86bdc9a2dfd67ef879fbb10426ea"

# Always build tools - they don't have any additional
# requirements over the library.
EXTRA_OEMESON = "-Dtools=enabled"

# Enable all project features for ptest
PACKAGECONFIG[tests] = " \
    -Dtests=enabled -Dtools=enabled -Dbindings-cxx=enabled -Dbindings-glib=enabled -Dgpioset-interactive=enabled -Ddbus=enabled, \
    -Dtests=disabled, \
    kmod util-linux glib-2.0 catch2 libedit glib-2.0-native libgudev, \
    bash ${VIRTUAL-RUNTIME_dbus} glib-2.0-utils libgpiod-manager-cfg shunit2 \
"
PACKAGECONFIG[cxx] = "-Dbindings-cxx=enabled,-Dbindings-cxx=disabled"
PACKAGECONFIG[gpioset-interactive] = "-D=gpioset-interactive=enabled,-Dgpioset-interactive=disabled,libedit"
PACKAGECONFIG[glib] = "-Dbindings-glib=enabled,-Dbindings-glib=disabled,glib-2.0 glib-2.0-native"
PACKAGECONFIG[dbus] = "-Ddbus=enabled,-Ddbus=disabled,glib-2.0 glib-2.0-native libgudev,${VIRTUAL-RUNTIME_dbus}"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

PACKAGES =+ " \
    ${PN}-gpiosim \
    ${PN}-glib \
    ${PN}-manager \
    ${PN}-manager-cfg \
    ${PN}-cli \
    libgpiotools \
    libgpiodbus \
"
FILES:${PN}-tools += "${bindir}/gpionotify"
FILES:${PN}-gpiosim += "${libdir}/libgpiosim.so.*"
FILES:${PN}-gpiosim-dev += "${includedir}/gpiosim.h"
FILES:${PN}-glib += " \
    ${libdir}/libgpiod-glib.so.* \
    ${libdir}/girepository-1.0/* \
"
FILES:${PN}-manager += " \
    ${bindir}/gpio-manager \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/gpio-manager.service', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', '${sysconfdir}/init.d/gpio-manager', '', d)} \
"
FILES:${PN}-manager-cfg += " \
    ${sysconfdir}/dbus-1/system.d/io.gpiod1.conf \
    ${datadir}/dbus-1/interfaces/io.gpiod1.xml \
    ${nonarch_base_libdir}/udev/rules.d/90-gpio.rules \
"
FILES:${PN}-cli += "${bindir}/gpiocli"
FILES:libgpiotools += "${libdir}/libgpiotools.so.*"
FILES:libgpiodbus += "${libdir}/libgpiodbus.so.*"

RDEPENDS:${PN}-manager += " \
    ${VIRTUAL-RUNTIME_dbus} \
    ${PN}-manager-cfg \
    libgpiodbus \
"
RDEPENDS:${PN}-cli += "${PN}-manager libgpiodbus"
RDEPENDS:${PN}-tools += "libgpiotools"

SYSTEMD_PACKAGES = "${PN}-manager"

python __anonymous() {
    distro_features = d.getVar("DISTRO_FEATURES").split()
    packageconfig = d.getVar("PACKAGECONFIG").split()
    pn = d.getVar("PN")

    if "systemd" in distro_features and "dbus" in packageconfig:
        d.appendVar("EXTRA_OEMESON", " -Dsystemd=enabled")
        # We need to set it conditionally or else the systemd class will look
        # for the file that we don't install with systemd support disabled.
        d.setVar("SYSTEMD_SERVICE:{}-manager".format(pn), "gpio-manager.service")
    else:
        d.appendVar("EXTRA_OECONF", " -Dsystemd=disabled")

    # Disable gobject introspection set by the bbclass if we don't want it.
    if not any(cfg in ["glib", "dbus", "ptest"] for cfg in packageconfig):
        d.setVar("GI_DATA_ENABLED", "False")
}

UPDATERCPN = "${PN}-manager"
INITSCRIPT_NAME = "gpio-manager"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 20 0 1 6 ."

USERADD_PACKAGES = "${PN}-manager"
GROUPADD_PARAM:${PN}-manager = "--system gpio"
USERADD_PARAM:${PN}-manager = "--system -M -s /bin/nologin -g gpio gpio-manager"

RDEPENDS:${PN}-ptest += " \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'shunit2 bash', '', d)} \
"
RRECOMMENDS:${PN}-gpiosim += "kernel-module-gpio-sim kernel-module-configfs"
INSANE_SKIP:${PN}-ptest += "buildpaths"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/gpio-manager.init ${D}${sysconfdir}/init.d/gpio-manager
    fi
}

do_install_ptest:append() {
    install -m 0755 ${B}/tests/gpiod-test ${D}${PTEST_PATH}/tests/
    for tool in ${FILES:${PN}-tools}; do
        install ${B}/tools/$(basename $tool) ${D}${PTEST_PATH}/tests/
    done
    install -m 0755 ${B}/bindings/cxx/tests/gpiod-cxx-test ${D}${PTEST_PATH}/tests/
    install -m 0755 ${S}/tools/gpio-tools-test.bash ${D}${PTEST_PATH}/tests/
    install -m 0644 ${S}/tests/scripts/gpiod-bash-test-helper.inc ${D}${PTEST_PATH}/tests/
    install -m 0644 ${S}/tests/gpiosim/gpiosim.h ${D}${includedir}/gpiosim.h
    install -m 0755 ${B}/bindings/glib/tests/gpiod-glib-test ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/dbus/tests/gpiodbus-test ${D}${PTEST_PATH}/tests/
    install -m 0755 ${S}/dbus/client/gpiocli-test.bash ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/dbus/manager/gpio-manager ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/dbus/client/gpiocli ${D}${PTEST_PATH}/tests/
}
