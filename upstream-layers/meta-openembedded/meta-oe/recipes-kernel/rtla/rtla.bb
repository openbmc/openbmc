SUMMARY = "Real-Time Linux Analysis tool"
LICENSE = "GPL-2.0-only"

DEPENDS = "libtraceevent libtracefs"
PROVIDES = "virtual/rtla"

B = "${WORKDIR}/${BPN}-${PV}"

inherit kernelsrc kernel-arch pkgconfig

do_populate_lic[depends] += "virtual/kernel:do_shared_workdir"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cpupower] = ",,cpupower"

EXTRA_OEMAKE = "V=1 -C ${S}/tools/tracing/rtla O=${B} CROSS=${TARGET_PREFIX} \
    CC="${CC} ${DEBUG_PREFIX_MAP} -D_GNU_SOURCE" LD="${LD}" AR=${AR} ARCH=${ARCH} \
    BUILD_BPF_SKEL=0"

do_compile() {
    # Older kernels (before 6.9) use a standalone rtla Makefile that does not honor
    # O= so outputs land in the kernel source tree (shared workdir). Remove
    # build output to make sure it is rebuilt. For newer kernels this wil be a no-op.
    rm -f ${S}/tools/tracing/rtla/rtla
    rm -f ${S}/tools/tracing/rtla/src/*.o

    # Older kernels need additional variable EXTRA_LDFLAGS to pick up correct linker options.
    export EXTRA_LDFLAGS="${LDFLAGS}"
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}

    # Older kernels (before 6.9) use a standalone rtla Makefile that does not honor
    # O= for the final binary, so it ends up in the kernel source tree.
    rtla_bin="${B}/rtla"
    [ -f "${rtla_bin}" ] || rtla_bin="${S}/tools/tracing/rtla/rtla"
    install -m 755 "${rtla_bin}" ${D}${bindir}/rtla

    # rtla, osnoise, hwnoise, and timerlat are all the same binary
    ln -s rtla ${D}${bindir}/osnoise
    ln -s rtla ${D}${bindir}/hwnoise
    ln -s rtla ${D}${bindir}/timerlat
}

PACKAGE_ARCH = "${MACHINE_ARCH}"
