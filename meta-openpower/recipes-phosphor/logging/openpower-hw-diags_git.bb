SUMMARY = "Hardware Diagnostics for POWER Systems"

DESCRIPTION = \
    "In the event of a system fatal error reported by the internal system \
    hardware (processor chips, memory chips, I/O chips, system memory, etc.), \
    POWER Systems have the ability to diagnose the root cause of the failure \
    and perform any service action needed to avoid repeated system failures."

HOMEPAGE = "https://github.com/openbmc/openpower-hw-diags"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PR = "r1"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/openpower-hw-diags;branch=master;protocol=https"
SRCREV = "08f25b219dab3da8bf82170a5c10b19ecf0f3882"

S = "${WORKDIR}/git"

inherit pkgconfig meson systemd

SYSTEMD_SERVICE:${PN} = "attn_handler.service"

DEPENDS = "boost libgpiod pdbg phosphor-logging sdbusplus openpower-libhei \
           nlohmann-json valijson fmt"

# This is required so that libhei is installed with the chip data files.
RDEPENDS:${PN} += "openpower-libhei"

# Conditionally pull in PHAL APIs, if available.
PACKAGECONFIG ??= "${@bb.utils.filter('MACHINE_FEATURES', 'phal', d)}"
PACKAGECONFIG[phal] = "-Dphal=enabled, -Dphal=disabled, ipl pdata"

# Don't build CI tests
EXTRA_OEMESON = "-Dtests=disabled"

pkg_postinst:${PN}() {
    mkdir -p $D$systemd_system_unitdir/obmc-host-startmin@0.target.wants
    LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.wants/attn_handler.service"
    TARGET="../attn_handler.service"
    ln -s $TARGET $LINK
}

pkg_prerm:${PN}() {
    LINK="$D$systemd_system_unitdir/obmc-host-startmin@0.target.wants/attn_handler.service"
    rm $LINK
}
