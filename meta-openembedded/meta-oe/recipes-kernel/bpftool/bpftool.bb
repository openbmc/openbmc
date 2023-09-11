SUMMARY = "Inspect and manipulate eBPF programs and maps"
DESCRIPTION = "bpftool is a kernel tool for inspection and simple manipulation \
of eBPF programs and maps."
LICENSE = "GPL-2.0-only"
DEPENDS = "binutils elfutils"
PROVIDES = "virtual/bpftool"

inherit bash-completion kernelsrc kernel-arch

do_populate_lic[depends] += "virtual/kernel:do_shared_workdir"

EXTRA_OEMAKE = "\
    V=1 \
    -C ${S}/tools/bpf/bpftool \
    O=${B} \
    CROSS=${TARGET_PREFIX} \
    CC="${CC} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH}" \
    LD="${LD}" \
    AR=${AR} \
    ARCH=${ARCH} \
    bash_compdir=${prefix}/share/bash-completion \
"

SECURITY_CFLAGS = ""

do_configure[depends] += "virtual/kernel:do_shared_workdir"

COMPATIBLE_HOST = "(x86_64|aarch64).*-linux"
COMPATIBLE_HOST:libc-musl = 'null'

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

FILES:${PN} += "${exec_prefix}/sbin/*"

BBCLASSEXTEND = "native nativesdk"
