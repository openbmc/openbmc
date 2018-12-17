SUMMARY = "haveged - A simple entropy daemon"
DESCRIPTION = "The haveged project is an attempt to provide an easy-to-use, unpredictable random number generator based upon an adaptation of the HAVEGE algorithm. Haveged was created to remedy low-entropy conditions in the Linux random device that can occur under some workloads, especially on headless servers."
AUTHOR = "Gary Wuertz"
HOMEPAGE = "http://www.issihosts.com/haveged/index.html"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM="file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://www.issihosts.com/haveged/haveged-${PV}.tar.gz \
           file://haveged-init.d-Makefile.am-add-missing-dependency.patch \
"

SRC_URI[md5sum] = "fb1d8b3dcbb9d06b30eccd8aa500fd31"
SRC_URI[sha256sum] = "f77d9adbdf421b61601fa29faa9ce3b479d910f73c66b9e364ba8642ccbfbe70"

UPSTREAM_CHECK_URI = "http://www.issihosts.com/haveged/downloads.html"

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
