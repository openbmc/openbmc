SUMMARY = "Merge machine and distro options to create a basic machine task/package"
PR = "r83"

#
# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
            packagegroup-base \
            packagegroup-base-extended \
            packagegroup-distro-base \
            packagegroup-machine-base \
            \
            ${@bb.utils.contains("MACHINE_FEATURES", "acpi", "packagegroup-base-acpi", "",d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "alsa", "packagegroup-base-alsa", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "apm", "packagegroup-base-apm", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "ext2", "packagegroup-base-ext2", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "vfat", "packagegroup-base-vfat", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "keyboard", "packagegroup-base-keyboard", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "pci", "packagegroup-base-pci", "",d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "pcmcia", "packagegroup-base-pcmcia", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "phone", "packagegroup-base-phone", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "serial", "packagegroup-base-serial", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "usbgadget", "packagegroup-base-usbgadget", "", d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "usbhost", "packagegroup-base-usbhost", "", d)} \
            \
            ${@bb.utils.contains("DISTRO_FEATURES", "bluetooth", "packagegroup-base-bluetooth", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "wifi", "packagegroup-base-wifi", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "3g", "packagegroup-base-3g", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "nfc", "packagegroup-base-nfc", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "cramfs", "packagegroup-base-cramfs", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "ipsec", "packagegroup-base-ipsec", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "packagegroup-base-ipv6", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "nfs", "packagegroup-base-nfs", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "ppp", "packagegroup-base-ppp", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "smbfs", "packagegroup-base-smbfs", "", d)} \
            ${@bb.utils.contains("DISTRO_FEATURES", "zeroconf", "packagegroup-base-zeroconf", "", d)} \
            \
            '

# Override by distro if needed
VIRTUAL-RUNTIME_keymaps ?= "keymaps"

#
# packagegroup-base contain stuff needed for base system (machine related)
#
RDEPENDS_packagegroup-base = "\
    packagegroup-distro-base \
    packagegroup-machine-base \
    \
    module-init-tools \
    ${@bb.utils.contains('MACHINE_FEATURES', 'apm', 'packagegroup-base-apm', '',d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', 'packagegroup-base-acpi', '',d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'keyboard', 'packagegroup-base-keyboard', '',d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'phone', 'packagegroup-base-phone', '',d)} \
    \
    ${@bb.utils.contains('COMBINED_FEATURES', 'alsa', 'packagegroup-base-alsa', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'ext2', 'packagegroup-base-ext2', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'vfat', 'packagegroup-base-vfat', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'pci', 'packagegroup-base-pci', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'pcmcia', 'packagegroup-base-pcmcia', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'usbgadget', 'packagegroup-base-usbgadget', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'usbhost', 'packagegroup-base-usbhost', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'bluetooth', 'packagegroup-base-bluetooth', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'wifi', 'packagegroup-base-wifi', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', '3g', 'packagegroup-base-3g', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'nfc', 'packagegroup-base-nfc', '',d)} \
    \
    ${@bb.utils.contains('DISTRO_FEATURES', 'nfs', 'packagegroup-base-nfs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'cramfs', 'packagegroup-base-cramfs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'smbfs', 'packagegroup-base-smbfs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'packagegroup-base-ipv6', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipsec', 'packagegroup-base-ipsec', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ppp', 'packagegroup-base-ppp', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'packagegroup-base-zeroconf', '',d)} \
    "


RRECOMMENDS_packagegroup-base = "\
    kernel-module-nls-utf8 \
    kernel-module-input \
    kernel-module-uinput \
    kernel-module-rtc-dev \
    kernel-module-rtc-proc \
    kernel-module-rtc-sysfs \
    kernel-module-unix"

RDEPENDS_packagegroup-base-extended = "\
    packagegroup-base \
    ${ADD_WIFI} \
    ${ADD_BT} \
    ${ADD_3G} \
    ${ADD_NFC} \
    "

ADD_WIFI = ""
ADD_BT = ""
ADD_3G = ""
ADD_NFC = ""

python __anonymous () {
    # If Distro want wifi and machine feature wifi/pci/pcmcia/usbhost (one of them)
    # then include packagegroup-base-wifi in packagegroup-base

    distro_features = set(d.getVar("DISTRO_FEATURES").split())
    machine_features= set(d.getVar("MACHINE_FEATURES").split())

    if "bluetooth" in distro_features and not "bluetooth" in machine_features and ("pcmcia" in machine_features or "pci" in machine_features or "usbhost" in machine_features):
        d.setVar("ADD_BT", "${MLPREFIX}packagegroup-base-bluetooth")

    if "wifi" in distro_features and not "wifi" in machine_features and ("pcmcia" in machine_features or "pci" in machine_features or "usbhost" in machine_features):
        d.setVar("ADD_WIFI", "${MLPREFIX}packagegroup-base-wifi")

    if "3g" in distro_features and not "3g" in machine_features and ("pcmcia" in machine_features or "pci" in machine_features or "usbhost" in machine_features):
        d.setVar("ADD_3G", "${MLPREFIX}packagegroup-base-3g")

    if "nfc" in distro_features and not "nfc" in machine_features and ("usbhost" in machine_features):
        d.setVar("ADD_NFC", "${MLPREFIX}packagegroup-base-nfc")
}

#
# packages added by distribution
#
SUMMARY_packagegroup-distro-base = "${DISTRO} extras"
DEPENDS_packagegroup-distro-base = "${DISTRO_EXTRA_DEPENDS}"
RDEPENDS_packagegroup-distro-base = "${DISTRO_EXTRA_RDEPENDS}"
RRECOMMENDS_packagegroup-distro-base = "${DISTRO_EXTRA_RRECOMMENDS}"

#
# packages added by machine config
#
SUMMARY_packagegroup-machine-base = "${MACHINE} extras"
SUMMARY_packagegroup-machine-base = "Extra packages required to fully support ${MACHINE} hardware"
RDEPENDS_packagegroup-machine-base = "${MACHINE_EXTRA_RDEPENDS}"
RRECOMMENDS_packagegroup-machine-base = "${MACHINE_EXTRA_RRECOMMENDS}"

SUMMARY_packagegroup-base-keyboard = "Keyboard support"
RDEPENDS_packagegroup-base-keyboard = "\
    ${VIRTUAL-RUNTIME_keymaps}"

SUMMARY_packagegroup-base-pci = "PCI bus support"
RDEPENDS_packagegroup-base-pci = "\
    pciutils"

SUMMARY_packagegroup-base-acpi = "ACPI support"
RDEPENDS_packagegroup-base-acpi = "\
    acpid"

SUMMARY_packagegroup-base-apm = "APM support"
RDEPENDS_packagegroup-base-apm = "\
    ${VIRTUAL-RUNTIME_apm} \
    apmd"

SUMMARY_packagegroup-base-ext2 = "ext2 filesystem support"
RDEPENDS_packagegroup-base-ext2 = "\
    hdparm \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs"

SUMMARY_packagegroup-base-vfat = "FAT filesystem support"
RRECOMMENDS_packagegroup-base-vfat = "\
    kernel-module-msdos \
    kernel-module-vfat \
    kernel-module-nls-iso8859-1 \
    kernel-module-nls-cp437 \
    dosfstools"

SUMMARY_packagegroup-base-alsa = "ALSA sound support"
RDEPENDS_packagegroup-base-alsa = "\
    alsa-utils-alsactl \
    alsa-utils-alsamixer \
    ${VIRTUAL-RUNTIME_alsa-state}"

RRECOMMENDS_packagegroup-base-alsa = "\
    kernel-module-snd-mixer-oss \
    kernel-module-snd-pcm-oss"

SUMMARY_packagegroup-base-pcmcia = "PC card slot support"
RDEPENDS_packagegroup-base-pcmcia = "\
    pcmciautils \
    "

RRECOMMENDS_packagegroup-base-pcmcia = "\
    kernel-module-pcmcia \
    kernel-module-airo-cs \
    kernel-module-pcnet-cs \
    kernel-module-serial-cs \
    kernel-module-ide-cs \
    kernel-module-ide-disk \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'kernel-module-hostap-cs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'kernel-module-orinoco-cs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'kernel-module-spectrum-cs', '',d)}"

SUMMARY_packagegroup-base-bluetooth = "Bluetooth support"
RDEPENDS_packagegroup-base-bluetooth = "\
    bluez5 \
    "

RRECOMMENDS_packagegroup-base-bluetooth = "\
    kernel-module-bluetooth \
    kernel-module-l2cap \
    kernel-module-rfcomm \
    kernel-module-hci-vhci \
    kernel-module-bnep \
    kernel-module-hidp \
    kernel-module-hci-uart \
    kernel-module-sco \
    ${@bb.utils.contains('COMBINED_FEATURES', 'usbhost', 'kernel-module-hci-usb', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'pcmcia', 'kernel-module-bluetooth3c-cs', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'pcmcia', 'kernel-module-bluecard-cs', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'pcmcia', 'kernel-module-bluetoothuart-cs', '',d)} \
    ${@bb.utils.contains('COMBINED_FEATURES', 'pcmcia', 'kernel-module-dtl1-cs', '',d)} \
    "

SUMMARY_packagegroup-base-usbgadget = "USB gadget support"
RRECOMMENDS_packagegroup-base-usbgadget = "\
    kernel-module-pxa27x_udc \
    kernel-module-gadgetfs \
    kernel-module-g-file-storage \
    kernel-module-g-serial \
    kernel-module-g-ether"

SUMMARY_packagegroup-base-usbhost = "USB host support"
RDEPENDS_packagegroup-base-usbhost = "\
    usbutils "

RRECOMMENDS_packagegroup-base-usbhost = "\
    kernel-module-uhci-hcd \
    kernel-module-ohci-hcd \
    kernel-module-ehci-hcd \
    kernel-module-usbcore \
    kernel-module-usbhid \
    kernel-module-usbnet \
    kernel-module-sd-mod \
    kernel-module-scsi-mod \
    kernel-module-usbmouse \
    kernel-module-mousedev \
    kernel-module-usbserial \
    kernel-module-usb-storage "

SUMMARY_packagegroup-base-ppp = "PPP dial-up protocol support"
RDEPENDS_packagegroup-base-ppp = "\
    ppp \
    ppp-dialin"

RRECOMMENDS_packagegroup-base-ppp = "\
    kernel-module-ppp-async \
    kernel-module-ppp-deflate \
    kernel-module-ppp-generic \
    kernel-module-ppp-mppe \
    kernel-module-slhc"

SUMMARY_packagegroup-base-ipsec = "IPSEC support"
RDEPENDS_packagegroup-base-ipsec = "\
    "

RRECOMMENDS_packagegroup-base-ipsec = "\
    kernel-module-ipsec"

#
# packagegroup-base-wifi contain everything needed to get WiFi working
# WEP/WPA connection needs to be supported out-of-box
#
SUMMARY_packagegroup-base-wifi = "WiFi support"
RDEPENDS_packagegroup-base-wifi = "\
    iw \
    wireless-regdb-static \
    wpa-supplicant"

RRECOMMENDS_packagegroup-base-wifi = "\
    ${@bb.utils.contains('COMBINED_FEATURES', 'usbhost', 'kernel-module-zd1211rw', '',d)} \
    kernel-module-ieee80211-crypt \
    kernel-module-ieee80211-crypt-ccmp \
    kernel-module-ieee80211-crypt-tkip \
    kernel-module-ieee80211-crypt-wep \
    kernel-module-ecb \
    kernel-module-arc4 \
    kernel-module-crypto_algapi \
    kernel-module-cryptomgr \
    kernel-module-michael-mic \
    kernel-module-aes-generic \
    kernel-module-aes"

SUMMARY_packagegroup-base-nfc = "Near Field Communication support"
RDEPENDS_packagegroup-base-nfc = "\
    neard"

RRECOMMENDS_packagegroup-base-nfc = "\
    kernel-module-nfc"

SUMMARY_packagegroup-base-3g = "Cellular data support"
RDEPENDS_packagegroup-base-3g = "\
    ofono"

RRECOMMENDS_packagegroup-base-3g = "\
    kernel-module-cdc-acm \
    kernel-module-cdc-wdm"

SUMMARY_packagegroup-base-smbfs = "SMB network filesystem support"
RRECOMMENDS_packagegroup-base-smbfs = "\
    kernel-module-cifs \
    kernel-module-smbfs"

SUMMARY_packagegroup-base-cramfs = "cramfs filesystem support"
RRECOMMENDS_packagegroup-base-cramfs = "\
    kernel-module-cramfs"

#
# packagegroup-base-nfs provides ONLY client support - server is in nfs-utils package
#
SUMMARY_packagegroup-base-nfs = "NFS network filesystem support"
RDEPENDS_packagegroup-base-nfs = "\
    rpcbind"

RRECOMMENDS_packagegroup-base-nfs = "\
    kernel-module-nfs "

SUMMARY_packagegroup-base-zeroconf = "Zeroconf support"
RDEPENDS_packagegroup-base-zeroconf = "\
    avahi-daemon"
RDEPENDS_packagegroup-base-zeroconf_append_libc-glibc = "\
    libnss-mdns \
    "

SUMMARY_packagegroup-base-ipv6 = "IPv6 support"
RDEPENDS_packagegroup-base-ipv6 = "\
    "

RRECOMMENDS_packagegroup-base-ipv6 = "\
    kernel-module-ipv6 "

SUMMARY_packagegroup-base-serial = "Serial port support"
RDEPENDS_packagegroup-base-serial = "\
    setserial \
    lrzsz "

SUMMARY_packagegroup-base-phone = "Cellular telephony (voice) support"
RDEPENDS_packagegroup-base-phone = "\
    ofono"
