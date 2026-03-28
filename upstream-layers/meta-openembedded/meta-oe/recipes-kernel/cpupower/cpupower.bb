SUMMARY = "Shows and sets processor power related values"
DESCRIPTION = "cpupower is a collection of tools to examine and tune power \
saving related features of your processor."
LICENSE = "GPL-2.0-only"
DEPENDS = "pciutils gettext-native"
PROVIDES = "virtual/cpupower"

B = "${WORKDIR}/${BPN}-${PV}"

inherit kernelsrc kernel-arch bash-completion

do_populate_lic[depends] += "virtual/kernel:do_shared_workdir"

EXTRA_OEMAKE = "-C ${S}/tools/power/cpupower O=${B} CROSS=${TARGET_PREFIX} CC="${CC}" LD="${LD}" AR=${AR} ARCH=${ARCH}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
    chown -R root:root ${D}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
PACKAGES =+ "${PN}-systemd"

FILES:${PN}-systemd = "${sysconfdir}/cpupower-service.conf ${systemd_unitdir}"

RDEPENDS:${PN} = "bash"

python do_package:prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION").split("-")[0])
}
