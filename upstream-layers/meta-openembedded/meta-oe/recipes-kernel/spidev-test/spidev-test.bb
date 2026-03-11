SUMMARY = "Test SPI devices"
DESCRIPTION = "SPI testing utility using the spidev driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"
PROVIDES = "virtual/spidev-test"

inherit bash-completion kernelsrc kernel-arch

do_populate_lic[depends] += "virtual/kernel:do_shared_workdir"

EXTRA_OEMAKE = "-C ${S}/tools/spi O=${B} CROSS=${TARGET_PREFIX} CC="${CC}" LD="${LD}" AR=${AR} ARCH=${ARCH}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

python do_package:prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION").split("-")[0])
}

B = "${WORKDIR}/${BPN}-${PV}"
