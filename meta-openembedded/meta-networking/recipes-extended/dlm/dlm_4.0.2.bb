DESCRIPTION = "dlm control daemon and tool"

SECTION = "utils"
HOMEPAGE = "https://fedorahosted.org/cluster/wiki/HomePage"

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI = "https://git.fedorahosted.org/cgit/dlm.git/snapshot/${BP}.tar.xz \
    file://respect-ldflags-also-from-bin_ldflags.patch \
"

SRC_URI[md5sum] = "efc2ee6093aa6aa0a88aaad83e998a3f"
SRC_URI[sha256sum] = "b89bc557aaffbab0ac005398025f247718a5589cff6574d902eaffe2b20e683e"

LICENSE = "LGPLv2+ & GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = "file://README.license;md5=8f0bbcdd678df1bce9863492b6c8832d"

DEPENDS = "corosync systemd"

inherit pkgconfig systemd distro_features_check

SYSTEMD_SERVICE_${PN} = "dlm.service"
SYSTEMD_AUTO_ENABLE = "enable"

export EXTRA_OEMAKE = ""

do_compile_prepend() {
    sed -i "s/libsystemd-daemon/libsystemd/g" ${S}/dlm_controld/Makefile
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

