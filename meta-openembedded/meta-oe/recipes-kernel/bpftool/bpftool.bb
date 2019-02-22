SUMMARY = "Inspect and manipulate eBPF programs and maps"
DESCRIPTION = "bpftool is a kernel tool for inspection and simple manipulation \
of eBPF programs and maps."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"
DEPENDS = "binutils elfutils"
PROVIDES = "virtual/bpftool"

inherit bash-completion kernelsrc kernel-arch

do_populate_lic[depends] += "virtual/kernel:do_patch"

EXTRA_OEMAKE = "-C ${S}/tools/bpf/bpftool O=${B} CROSS=${TARGET_PREFIX} CC="${CC}" LD="${LD}" AR=${AR} ARCH=${ARCH}"

do_configure[depends] += "virtual/kernel:do_shared_workdir"

COMPATIBLE_HOST_libc-musl = 'null'

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR=${D} install
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

python do_package_prepend() {
    d.setVar('PKGV', d.getVar("KERNEL_VERSION", True).split("-")[0])
}

B = "${WORKDIR}/${BPN}-${PV}"
