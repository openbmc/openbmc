DESCRIPTION = "dlm control daemon and tool"

SECTION = "utils"
HOMEPAGE = "https://fedorahosted.org/cluster/wiki/HomePage"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "https://pagure.io/dlm/archive/dlm-${PV}/dlm-dlm-${PV}.tar.gz \
           file://0001-Include-sys-sysmacros.h-for-major-minor-macros-in-gl.patch \
           file://0001-make-Replace-cp-a-with-mode-preserving-options.patch \
           file://0001-dlm_controld-remove-unnecessary-header-include.patch \
           file://0001-Disable-annobin-plugin.patch \
           file://0001-Remove-fcf-protection-full.patch \
           "

SRC_URI[sha256sum] = "90237e18af7422ac15fc756899b3bb6932597b13342296de8e0e120e6d8729ab"

UPSTREAM_CHECK_URI = "https://pagure.io/dlm/releases"
UPSTREAM_CHECK_REGEX = "dlm-(?P<pver>\d+(\.\d+)+)"

LICENSE = "LGPL-2.0-or-later & GPL-2.0-only & GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://README.license;md5=8f0bbcdd678df1bce9863492b6c8832d"

S = "${WORKDIR}/dlm-dlm-${PV}"

DEPENDS += "corosync"

inherit pkgconfig systemd features_check

PACKAGECONFIG ??= ""

PACKAGECONFIG[pacemaker] = ",,pacemaker"

SYSTEMD_SERVICE:${PN} = "dlm.service"
SYSTEMD_AUTO_ENABLE = "enable"

export EXTRA_OEMAKE = ""

CFPROTECTION ?= "-fcf-protection=full"
CFPROTECTION:riscv32 = ""
CFPROTECTION:riscv64 = ""
CFPROTECTION:arm = ""
CFPROTECTION:aarch64 = ""

CFLAGS += "${CFPROTECTION}"

PARALLEL_MAKE = ""

DONTBUILD = "${@bb.utils.contains('PACKAGECONFIG', 'pacemaker', '', 'fence', d)}"

do_compile() {
    sed -i "s/libsystemd-daemon/libsystemd/g" ${S}/dlm_controld/Makefile
    sed -i -e "s/ ${DONTBUILD}//g" ${S}/Makefile
    oe_runmake 'CC=${CC}'
}

do_install() {
    oe_runmake install DESTDIR=${D} LIBDIR=${libdir}
    install -Dm 0644 ${S}/init/dlm.sysconfig ${D}${sysconfdir}/sysconfig/dlm
    install -Dm 0644 ${S}/init/dlm.init ${D}${sysconfdir}/init.d/dlm

    # install systemd unit files
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -Dm 0644 ${S}/init/dlm.service ${D}${systemd_unitdir}/system/dlm.service
    fi
}
