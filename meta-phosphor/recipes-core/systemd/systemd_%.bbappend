FILES:${PN}-catalog-extralocales = \
            "${exec_prefix}/lib/systemd/catalog/*.*.catalog"
PACKAGES =+ "${PN}-catalog-extralocales"
PACKAGECONFIG = "\
        coredump \
        hostnamed \
        kmod \
        networkd \
        nss \
        pam \
        randomseed \
        resolved \
        seccomp \
        sysusers \
        sysvinit \
        timedated \
        timesyncd \
        zstd \
        "

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://0001-sd-bus-Don-t-automatically-add-ObjectManager.patch"

EXTRA_OEMESON:append = " -Ddns-servers=''"

ALTERNATIVE:${PN} += "init"
ALTERNATIVE_TARGET[init] = "${rootlibexecdir}/systemd/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"

RRECOMMENDS:${PN}:append:openbmc-phosphor = " phosphor-systemd-policy"
