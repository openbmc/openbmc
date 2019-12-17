SUMMARY = "NVMe management command line interface"
AUTHOR = "Stefan Wiehler <stefan.wiehler@missinglinkelectronics.com>"
HOMEPAGE = "https://github.com/linux-nvme/nvme-cli"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8264535c0c4e9c6c335635c4026a8022"
DEPENDS = "util-linux"
PV .= "+git${SRCPV}"

SRC_URI = "git://github.com/linux-nvme/nvme-cli.git"
SRCREV = "977e7d4cf59c3c7f89e9c093c91f991b07292e45"

S = "${WORKDIR}/git"

inherit bash-completion systemd

do_install() {
    oe_runmake install-spec DESTDIR=${D} PREFIX=${prefix} \
        UDEVDIR=${nonarch_base_libdir}/udev SYSTEMDDIR=${systemd_unitdir}
}

pkg_postinst_ontarget_${PN}() {
    ${sbindir}/nvme gen-hostnqn > ${sysconfdir}/nvme/hostnqn
    ${bindir}/uuidgen > ${sysconfdir}/nvme/hostid
}

PACKAGES =+ "${PN}-dracut ${PN}-zsh-completion"

FILES_${PN} += "${systemd_system_unitdir}"
FILES_${PN}-dracut = "${libdir}/dracut/dracut.conf.d"
FILES_${PN}-zsh-completion = "${datadir}/zsh/site-functions"

RDEPENDS_${PN} = "util-linux-uuidgen"
