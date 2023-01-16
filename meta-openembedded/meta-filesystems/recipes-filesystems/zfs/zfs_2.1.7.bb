SUMMARY = "OpenZFS on Linux and FreeBSD"
DESCRIPTION = "OpenZFS on Linux and FreeBSD"
LICENSE = "CDDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7087caaf1dc8a2856585619f4a787faa"
HOMEPAGE ="https://github.com/openzfs/zfs"

SRC_URI = "https://github.com/openzfs/zfs/releases/download/${BPN}-${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Define-strndupa-if-it-does-not-exist.patch \
"
SRC_URI[sha256sum] = "6462e63e185de6ff10c64ffa6ed773201a082f9dd13e603d7e8136fcb4aca71b"

# Using both 'module' and 'autotools' classes seems a bit odd, they both
# define a do_compile function.
# That's why we opt for module-base, also this prevents module splitting.
inherit module-base pkgconfig autotools

DEPENDS = "virtual/kernel zlib util-linux libtirpc openssl curl"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd sysvinit', d)}"

PACKAGECONFIG[pam] = "--enable-pam --with-pamconfigsdir=${datadir}/pam-configs --with-pammoduledir=${libdir}/security, --disable-pam"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,"
PACKAGECONFIG[sysvinit] = "--enable-sysvinit,--disable-sysvinit,"

EXTRA_OECONF:append = " \
    --disable-pyzfs \
    --with-linux=${STAGING_KERNEL_DIR} --with-linux-obj=${STAGING_KERNEL_BUILDDIR} \
    --with-mounthelperdir=${base_sbin} \
    --with-udevdir=${base_libdir}/udev \
    --without-dracutdir \
    "

EXTRA_OEMAKE:append = " \
    INSTALL_MOD_PATH=${D}${root_prefix} \
    "

do_install:append() {
    # /usr/share/zfs contains the zfs-tests folder which we do not need:
    rm -rf ${D}${datadir}/zfs

    rm -rf ${D}${datadir}/initramfs-tools
}

FILES:${PN} += "\
    ${base_sbindir}/* \
    ${base_libdir}/* \
    ${sysconfdir}/* \
    ${sbindir}/* \
    ${bindir}/* \
    ${libexecdir}/${BPN}/* \
    ${libdir}/* \
    "

FILES:${PN}-dev += "\
    ${prefix}/src/zfs-${PV}/* \
    ${prefix}/src/spl-${PV}/* \
    "
# Not yet ported to rv32
COMPATIBLE_HOST:riscv32 = "null"
# conflicting definition of ABS macro from asm/asm.h from kernel
COMPATIBLE_HOST:mips = "null"
