SUMMARY = "NVMe management command line interface"
HOMEPAGE = "https://github.com/linux-nvme/nvme-cli"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only & CC0-1.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8264535c0c4e9c6c335635c4026a8022 \
                    file://ccan/licenses/CC0;md5=c17af43b05840255a6fedc5eda9d56cc \
                    file://ccan/licenses/BSD-MIT;md5=838c366f69b72c5df05c96dff79b35f2"
DEPENDS = "json-c libnvme"
SRCREV = "b340fd7dcf1aef76f8d46ab28bef3c170d310887"

SRC_URI = "git://github.com/linux-nvme/nvme-cli.git;branch=master;protocol=https \
           file://0001-nvme-Use-C99-types-for-uint32_t.patch \
           file://0001-plugins-ssstc-Replace-__uint16_t-with-uint16_t.patch \
           "

S = "${WORKDIR}/git"

inherit bash-completion meson pkgconfig systemd

EXTRA_OEMESON += "-Dsystemddir=${systemd_unitdir}/system"

pkg_postinst_ontarget:${PN}-user () {
    ${sbindir}/nvme gen-hostnqn > ${sysconfdir}/nvme/hostnqn
    ${bindir}/uuidgen > ${sysconfdir}/nvme/hostid
}

PACKAGES =+ "${PN}-dracut ${PN}-zsh-completion ${PN}-user"

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN}-dracut = "${nonarch_libdir}/dracut/dracut.conf.d"
FILES:${PN}-zsh-completion = "${datadir}/zsh/site-functions"
ALLOW_EMPTY:${PN}-user = "1"

RDEPENDS:${PN}-user = "util-linux-uuidgen"
