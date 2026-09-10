SUMMARY = "cntvct-log - ARM virtual counter boot time logger"
DESCRIPTION = "Userspace tool to log ARM CNTVCT_EL0 counter values for boot time analysis"
HOMEPAGE = "https://gitlab.com/CentOS/automotive/src/boot-time-analysis-tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=1c2e0cc0dec0b709fe547806b55737b0"

SRC_URI = "git://gitlab.com/CentOS/automotive/src/boot-time-analysis-tools.git;protocol=https;branch=main;tag=${PV}"
SRCREV = "2409ce37e7389d8079bdb8740cb6a44b505fb8d4"

S = "${UNPACKDIR}/cntvct-log-${PV}/cntvct-log"

inherit meson systemd features_check

# cntvct@.service is a systemd template unit; no function without systemd.
REQUIRED_DISTRO_FEATURES = "systemd"

# cntvct-log contains AArch64-specific inline assembly.
COMPATIBLE_HOST = "aarch64.*-linux"

SYSTEMD_SERVICE:${PN} = "cntvct@.service"
SYSTEMD_AUTO_ENABLE = "disable"

# The Meson build does not install the service file in cross-compilation
# environments; install it explicitly from the source tree layout.
do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/usr/lib/systemd/system/cntvct@.service \
        ${D}${systemd_system_unitdir}/cntvct@.service
}

FILES:${PN} += "${systemd_system_unitdir}/cntvct@.service"
