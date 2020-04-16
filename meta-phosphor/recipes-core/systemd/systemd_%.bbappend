FILES_${PN}-catalog-extralocales = \
            "${exec_prefix}/lib/systemd/catalog/*.*.catalog"
PACKAGES =+ "${PN}-catalog-extralocales"
PACKAGECONFIG = "pam hostnamed networkd randomseed resolved sysusers timedated \
                 timesyncd xz kmod coredump"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://default.network"
SRC_URI += "file://0001-sd-bus-Don-t-automatically-add-ObjectManager.patch"

FILES_${PN} += "${systemd_unitdir}/network/default.network"
EXTRA_OEMESON += "-Ddns-servers=''"

do_install_append() {
        install -m 644 ${WORKDIR}/default.network ${D}${systemd_unitdir}/network/
}

ALTERNATIVE_${PN} += "init"
ALTERNATIVE_TARGET[init] = "${rootlibexecdir}/systemd/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"
