SUMMARY = "NVMe management command line interface"
HOMEPAGE = "https://github.com/linux-nvme/nvme-cli"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only & CC0-1.0 & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8264535c0c4e9c6c335635c4026a8022 \
                    file://ccan/licenses/CC0;md5=c17af43b05840255a6fedc5eda9d56cc \
                    file://ccan/licenses/BSD-MIT;md5=838c366f69b72c5df05c96dff79b35f2"
DEPENDS = "json-c libnvme"
SRCREV = "9f34fcb12e3ab098e9b30e0f31e92cefb497cc42"

SRC_URI = "git://github.com/linux-nvme/nvme-cli.git;branch=master;protocol=https \
           file://0001-nvme-print-add-fallback-for-non-standard-locale-cate.patch \
           file://0002-plugins-netapp-add-include-of-libgen.h-for-basename-.patch"

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

# This one is reproducible only on 32bit MACHINEs
# http://errors.yoctoproject.org/Errors/Details/766964/
# git/plugins/virtium/virtium-nvme.c:205:63: error: passing argument 1 of 'localtime' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
