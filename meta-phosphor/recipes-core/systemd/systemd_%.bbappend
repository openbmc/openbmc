FILES_${PN}-catalog-extralocales = \
            "${exec_prefix}/lib/systemd/catalog/*.*.catalog"
PACKAGES =+ "${PN}-catalog-extralocales"
PACKAGECONFIG_remove = "backlight binfmt firstboot hibernate ima \
                        localed logind machined polkit quotacheck smack utmp \
                        vconsole"
FILESEXTRAPATHS_append := "${THISDIR}/${PN}:"
SRC_URI += "file://default.network"
SRC_URI += "file://0001-sd-bus-Don-t-automatically-add-ObjectManager.patch"
SRC_URI += "file://0006-core-fix-the-check-if-CONFIG_CGROUP_BPF-is-on.patch"

# Fixes from upstream for openbmc/openbmc#3459
SRC_URI += "file://0001-timedate-defer-the-property-changed-signal-until-job.patch"
SRC_URI += "file://0001-timedate-treat-activating-or-inactivating-NTP-client.patch"
SRC_URI += "file://0002-timedate-refuse-to-set-time-when-previous-request-is.patch"

RRECOMMENDS_${PN} += "obmc-targets"
FILES_${PN} += "${systemd_unitdir}/network/default.network"

do_install_append() {
        install -m 644 ${WORKDIR}/default.network ${D}${systemd_unitdir}/network/
}

ALTERNATIVE_${PN} += "init"
ALTERNATIVE_TARGET[init] = "${rootlibexecdir}/systemd/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"
