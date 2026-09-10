LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/apalrd/tayga.git;branch=main;protocol=https;tag=${PV}"
SRCREV = "7f00122b75d808f696df0efe481a8c8919ba9bd7"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "tayga@.service"
SYSTEMD_AUTO_ENABLE = "disable"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

do_configure[noexec] = "1"

do_compile() {
  oe_runmake
}

do_install() {
  oe_runmake install \
    DESTDIR=${D} \
    prefix=${prefix} \
    exec_prefix=${exec_prefix} \
    sbindir=${sbindir} \
    datarootdir=${datadir} \
    mandir=${mandir} \
    sysconfdir=${sysconfdir} \
    servicedir=${systemd_system_unitdir} \
    WITH_SYSTEMD=1

  install -d ${D}${sysconfdir}/tayga
  install -m 0644 ${S}/tayga.conf.example ${D}${sysconfdir}/tayga/default.conf
}

inherit systemd

RRECOMMENDS:${PN} += "kernel-module-tun"