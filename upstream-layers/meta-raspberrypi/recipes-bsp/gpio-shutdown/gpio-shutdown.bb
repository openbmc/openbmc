SUMMARY = "GPIO shutdown bindings for SysV init"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://bind_gpio_shutdown.tab \
    file://gpio-shutdown-keymap.sh \
"

S = "${UNPACKDIR}"

inherit  update-rc.d

INITSCRIPT_NAME = "gpio-shutdown-keymap.sh"
# Run only once during startup
INITSCRIPT_PARAMS = "start 99 S ."

do_install() {
    # The files are only needed if using SysV init.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir} \
            ${D}${sysconfdir}/inittab.d \
            ${D}${sysconfdir}/init.d

        install -m 0755 ${UNPACKDIR}/gpio-shutdown-keymap.sh ${D}${sysconfdir}/init.d/
        install -m 0755 ${UNPACKDIR}/bind_gpio_shutdown.tab ${D}${sysconfdir}/inittab.d/
    elif ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        # Systemd init does not require any configuration.
        # Note: cannot have an empty branch, hence the redundant dir install.
        install -d ${D}${sysconfdir}
    else
        bbwarn "Not using sysvinit or systemd. The gpio-shutdown may require additional configuration."
    fi
}
