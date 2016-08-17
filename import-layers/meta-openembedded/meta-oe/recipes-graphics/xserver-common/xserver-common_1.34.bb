SUMMARY = "Common X11 scripts and support files"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

PR = "r8"

# we are using a gpe-style Makefile
inherit gpe

SRC_URI[md5sum] = "82f2f84cd96610e8f7b92c700cd31c14"
SRC_URI[sha256sum] = "cd04c33418f776b1e13fcc7af3d6bd0c7cccd03fbabd7dbcd97f88166cc34210"

SRC_URI_append = " \
    file://0001-COPYING-add-GPLv2-license-file.patch \
    file://0002-add-setdpi-Xinit.d-script.patch \
    file://0003-add-89xdgautostart-Xsession.d-script.patch \
    file://0005-add-XWindowManager-Xsession.d-script.patch \
    file://0006-add-support-for-etc-X11-xserver-system.patch \
    file://0007-use-own-functions-file-instead-etc-init.d-functions.patch \
    file://0008-xserver-common-add-dpi-and-nocursor-params-for-gta01.patch \
    file://0009-xserver-common-add-support-for-n900-alias-nokia_rx-5.patch \
    file://0010-xserver-common-add-support-for-nexus-S-alias-herring.patch \
    file://0011-xserver-common-add-support-for-nexus-one-alias-mahim.patch \
    file://0012-xserver-common-add-support-for-gta04-alias-OpenPhoen.patch \
    file://0013-xserver-common-add-support-for-tuna-alias-Galaxy-Nex.patch \
    file://0014-Xserver-Start-Xsession-like-x11-common-does.patch \
    file://0015-xserver-common-disable-TCP-connections.patch \
"

do_install_append() {
    sed -i 's:^BINDIR=.*$:BINDIR=${bindir}:' ${D}/etc/X11/xserver-common
    # Rename all Xsession files not ending with .sh
    # Unfortunatelly when xinput-calibrator was moved to oe-core
    # its Xsession file got name 30xinput_calibrate.sh and ls -X sorts it
    # last, not respecting numbers for sorting them
    for i in ${D}/${sysconfdir}/X11/Xsession.d/*; do
        echo $i | grep '.sh$' || mv $i $i.sh
    done
}

RDEPENDS_${PN} = "xmodmap xrandr xdpyinfo fbset xinput-calibrator"

RCONFLICTS_${PN} = "xserver-kdrive-common x11-common"
RREPLACES_${PN} = "xserver-kdrive-common x11-common"

