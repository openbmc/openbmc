FILES_${PN}-catalog-extralocales = \
            "${exec_prefix}/lib/systemd/catalog/*.*.catalog"
PACKAGES =+ "${PN}-catalog-extralocales"
PACKAGECONFIG = "\
        coredump \
        hostnamed \
        kmod \
        networkd \
        pam \
        randomseed \
        resolved \
        sysusers \
        sysvinit \
        timedated \
        timesyncd \
        xz \
        "

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://0001-sd-bus-Don-t-automatically-add-ObjectManager.patch"

EXTRA_OEMESON += "-Ddns-servers=''"

ALTERNATIVE_${PN} += "init"
ALTERNATIVE_TARGET[init] = "${rootlibexecdir}/systemd/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"
