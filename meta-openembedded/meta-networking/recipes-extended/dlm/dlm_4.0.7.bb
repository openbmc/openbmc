DESCRIPTION = "dlm control daemon and tool"

SECTION = "utils"
HOMEPAGE = "https://fedorahosted.org/cluster/wiki/HomePage"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "https://releases.pagure.org/dlm/${BP}.tar.gz \
           file://respect-ldflags-also-from-bin_ldflags.patch \
           file://0001-dlm-fix-compile-error-since-xml2-config-should-not-b.patch \
           file://0001-dlm-fix-package-qa-error.patch \
           file://0001-Include-sys-sysmacros.h-for-major-minor-macros-in-gl.patch \
           "

SRC_URI[md5sum] = "aa604a10d5ac2d3414eb89ec6984cd12"
SRC_URI[sha256sum] = "639ddfc82369272a68d56816689736c00b8f1b6b2869a6b66b7dbf6dad86469a"

UPSTREAM_CHECK_URI = "https://pagure.io/dlm/releases"
UPSTREAM_CHECK_REGEX = "dlm-(?P<pver>\d+(\.\d+)+)"

LICENSE = "LGPLv2+ & GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://README.license;md5=8f0bbcdd678df1bce9863492b6c8832d"

DEPENDS = "corosync systemd"

inherit pkgconfig systemd distro_features_check

PACKAGECONFIG ??= ""

PACKAGECONFIG[pacemaker] = ",,pacemaker"

SYSTEMD_SERVICE_${PN} = "dlm.service"
SYSTEMD_AUTO_ENABLE = "enable"

export EXTRA_OEMAKE = ""

DONTBUILD = "${@bb.utils.contains('PACKAGECONFIG', 'pacemaker', '', 'fence', d)}"

do_compile_prepend() {
    sed -i "s/libsystemd-daemon/libsystemd/g" ${S}/dlm_controld/Makefile
    sed -i -e "s/ ${DONTBUILD}//g" ${S}/Makefile
}

do_compile () {
    oe_runmake 'CC=${CC}'
}

do_install_append (){
    install -d ${D}${sysconfdir}/sysconfig/
    install -d ${D}${sysconfdir}/init.d/
    install -m 0644 ${S}/init/dlm.sysconfig ${D}${sysconfdir}/sysconfig/dlm
    install -m 0644 ${S}/init/dlm.init ${D}${sysconfdir}/init.d/dlm

    # install systemd unit files
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/init/dlm.service ${D}${systemd_unitdir}/system
    fi
}

do_install() {
    oe_runmake install DESTDIR=${D} LIBDIR=${libdir}
}

