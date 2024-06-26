FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
PACKAGECONFIG = "\
        cgroupv2 \
        coredump \
        hostnamed \
        networkd \
        nss \
        pam \
        pstore \
        randomseed \
        resolved \
        seccomp \
        sysusers \
        timedated \
        timesyncd \
        zstd \
        "

EXTRA_OEMESON:append = " -Ddns-servers=''"

PACKAGES =+ "${PN}-catalog-extralocales"

RRECOMMENDS:${PN}:append:openbmc-phosphor = " phosphor-systemd-policy"

FILES:${PN}-catalog-extralocales = "\
    ${exec_prefix}/lib/systemd/catalog/*.*.catalog \
"

SRC_URI:append = " \
  file://40-hardware-watchdog.conf \
  "

FILES:${PN}:append = " \
  ${systemd_unitdir}/system.conf.d/40-hardware-watchdog.conf \
  "

do_install:append() {
    install -d -m 0755 ${D}${systemd_unitdir}/system.conf.d/
    install -m 0644 ${UNPACKDIR}/40-hardware-watchdog.conf ${D}${systemd_unitdir}/system.conf.d/
}

# udev is added to the USERADD_PACKAGES due to some 'render' group
# being necessary to create for /dev/dri handling, which we don't
# have to worry about.  A side-effect of this is udev would RDEPEND on
# 'shadow' which prevents us from putting it into the initramfs.  We
# have plenty of other stuff that RDEPENDS on 'shadow' so, remove udev
# from USERADD_PACKAGES to get around that.
USERADD_PACKAGES:remove = "udev"

ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"

ALTERNATIVE:${PN} += "init"
ALTERNATIVE_TARGET[init] = "${rootlibexecdir}/systemd/systemd"
