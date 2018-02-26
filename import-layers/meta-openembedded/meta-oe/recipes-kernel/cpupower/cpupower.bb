SUMMARY = "Shows and sets processor power related values"
DESCRIPTION = "cpupower is a collection of tools to examine and tune power \
saving related features of your processor."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"
DEPENDS = "pciutils"
PROVIDES = "virtual/cpupower"

inherit kernelsrc kernel-arch

do_populate_lic[depends] += "virtual/kernel:do_patch"

EXTRA_OEMAKE = "-C ${S}/tools/power/cpupower O=${B} CROSS=${TARGET_PREFIX} CC="${CC}" LD="${LD}" AR=${AR} ARCH=${ARCH}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
    # Do not ship headers
    rm -rf ${D}${includedir}
    chown -R root:root ${D}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS_${PN} = "bash"

python do_package_prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION", True).split("-")[0])
}

B = "${WORKDIR}/${BPN}-${PV}"
