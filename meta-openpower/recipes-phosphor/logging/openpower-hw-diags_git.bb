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

SRC_URI = "git://github.com/openbmc/openpower-hw-diags"
SRCREV = "5d63cefcada92a8588ab55fb2a741c898cb19a8b"

S = "${WORKDIR}/git"

inherit meson systemd

SYSTEMD_SERVICE:${PN} = "attn_handler.service"

DEPENDS = "boost libgpiod pdbg phosphor-logging sdbusplus openpower-libhei \
           nlohmann-json valijson"

# This is required so that libhei is installed with the chip data files.
RDEPENDS:${PN} += "openpower-libhei"

# Conditionally pull in PHAL APIs, if available.
PACKAGECONFIG ??= "${@bb.utils.filter('OBMC_MACHINE_FEATURES', 'phal', d)}"
PACKAGECONFIG[phal] = "-Dphal=enabled, -Dphal=disabled, pdata"

