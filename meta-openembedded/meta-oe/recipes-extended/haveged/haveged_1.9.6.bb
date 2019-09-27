SUMMARY = "haveged - A simple entropy daemon"
DESCRIPTION = "The haveged project is an attempt to provide an easy-to-use, unpredictable random number generator based upon an adaptation of the HAVEGE algorithm. Haveged was created to remedy low-entropy conditions in the Linux random device that can occur under some workloads, especially on headless servers."
AUTHOR = "Gary Wuertz"
HOMEPAGE = "http://www.issihosts.com/haveged/index.html"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM="file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# v1.9.6
SRCREV = "1470a82a7f79110c79beea1ca5f2d3b0fd1a4668"
SRC_URI = "git://github.com/jirka-h/haveged.git \
"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/jirka-h/haveged/releases"

inherit autotools update-rc.d systemd

EXTRA_OECONF = "\
    --enable-nistest=yes \
    --enable-olt=yes \
    --enable-threads=no \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--enable-init=service.redhat --enable-initdir=${systemd_system_unitdir}, --enable-init=sysv.redhat, systemd"

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "haveged"
INITSCRIPT_PARAMS_${PN} = "defaults 9"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "haveged.service"

do_install_append() {
    # The exit status is 143 when the service is stopped
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        sed -i '/ExecStart/a SuccessExitStatus=143' ${D}${systemd_system_unitdir}/haveged.service
    fi
}

MIPS_INSTRUCTION_SET = "mips"
