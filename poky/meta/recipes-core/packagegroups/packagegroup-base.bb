SUMMARY = "Merge machine and distro options to create a basic machine task/package"

#
# packages which content depend on MACHINE_FEATURES need to be MACHINE_ARCH
#
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PACKAGES = ' \
            packagegroup-base \
            packagegroup-base-extended \
            packagegroup-distro-base \
            packagegroup-machine-base \
            \
            ${@bb.utils.contains("MACHINE_FEATURES", "acpi", "packagegroup-base-acpi", "",d)} \
            ${@bb.utils.contains("MACHINE_FEATURES", "alsa", "packagegroup-base-alsa", "", d)} \
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
RDEPENDS:packagegroup-base = "\
    packagegroup-distro-base \
    packagegroup-machine-base \
    \
    module-init-tools \
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


RRECOMMENDS:packagegroup-base = "\
    kernel-module-nls-utf8 \
    kernel-module-input \
    kernel-module-uinput \
    kernel-module-rtc-dev \
    kernel-module-rtc-proc \
    kernel-module-rtc-sysfs \
    kernel-module-unix"

RDEPENDS:packagegroup-base-extended = "\
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
SUMMARY:packagegroup-distro-base = "${DISTRO} extras"
DEPENDS_packagegroup-distro-base = "${DISTRO_EXTRA_DEPENDS}"
RDEPENDS:packagegroup-distro-base = "${DISTRO_EXTRA_RDEPENDS}"
RRECOMMENDS:packagegroup-distro-base = "${DISTRO_EXTRA_RRECOMMENDS}"

#
# packages added by machine config
#
SUMMARY:packagegroup-machine-base = "Extra packages required to fully support ${MACHINE} hardware"
RDEPENDS:packagegroup-machine-base = "${MACHINE_EXTRA_RDEPENDS}"
RRECOMMENDS:packagegroup-machine-base = "${MACHINE_EXTRA_RRECOMMENDS}"

SUMMARY:packagegroup-base-keyboard = "Keyboard support"
RDEPENDS:packagegroup-base-keyboard = "\
    ${VIRTUAL-RUNTIME_keymaps}"

SUMMARY:packagegroup-base-pci = "PCI bus support"
RDEPENDS:packagegroup-base-pci = "\
    pciutils"

SUMMARY:packagegroup-base-acpi = "ACPI support"
RDEPENDS:packagegroup-base-acpi = "\
    acpid"

SUMMARY:packagegroup-base-ext2 = "ext2 filesystem support"
RDEPENDS:packagegroup-base-ext2 = "\
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs"

RRECOMMENDS:packagegroup-base-ext2 = "\
    hdparm \
    e2fsprogs"

SUMMARY:packagegroup-base-vfat = "FAT filesystem support"
RRECOMMENDS:packagegroup-base-vfat = "\
    kernel-module-msdos \
    kernel-module-vfat \
    kernel-module-nls-iso8859-1 \
    kernel-module-nls-cp437 \
    dosfstools"

SUMMARY:packagegroup-base-alsa = "ALSA sound support"
RDEPENDS:packagegroup-base-alsa = "\
    alsa-utils-alsactl \
    alsa-utils-amixer \
    ${VIRTUAL-RUNTIME_alsa-state}"

RRECOMMENDS:packagegroup-base-alsa = "\
    kernel-module-snd-mixer-oss \
    kernel-module-snd-pcm-oss"

SUMMARY:packagegroup-base-pcmcia = "PC card slot support"
RDEPENDS:packagegroup-base-pcmcia = "\
    pcmciautils \
    "

RRECOMMENDS:packagegroup-base-pcmcia = "\
    kernel-module-pcmcia \
    kernel-module-airo-cs \
    kernel-module-pcnet-cs \
    kernel-module-serial-cs \
    kernel-module-ide-cs \
    kernel-module-ide-disk \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'kernel-module-hostap-cs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'kernel-module-orinoco-cs', '',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'kernel-module-spectrum-cs', '',d)}"

SUMMARY:packagegroup-base-bluetooth = "Bluetooth support"
RDEPENDS:packagegroup-base-bluetooth = "\
    bluez5 \
    "

RRECOMMENDS:packagegroup-base-bluetooth = "\
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

SUMMARY:packagegroup-base-usbgadget = "USB gadget support"
RRECOMMENDS:packagegroup-base-usbgadget = "\
    kernel-module-pxa27x_udc \
    kernel-module-gadgetfs \
    kernel-module-g-file-storage \
    kernel-module-g-serial \
    kernel-module-g-ether"

SUMMARY:packagegroup-base-usbhost = "USB host support"
RDEPENDS:packagegroup-base-usbhost = "\
    usbutils "

RRECOMMENDS:packagegroup-base-usbhost = "\
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

SUMMARY:packagegroup-base-ppp = "PPP dial-up protocol support"
RDEPENDS:packagegroup-base-ppp = "\
    ppp \
    ppp-dialin"

RRECOMMENDS:packagegroup-base-ppp = "\
    kernel-module-ppp-async \
    kernel-module-ppp-deflate \
    kernel-module-ppp-generic \
    kernel-module-ppp-mppe \
    kernel-module-slhc"

SUMMARY:packagegroup-base-ipsec = "IPSEC support"
RDEPENDS:packagegroup-base-ipsec = "\
    "

RRECOMMENDS:packagegroup-base-ipsec = "\
    kernel-module-ipsec"

#
# packagegroup-base-wifi contain everything needed to get WiFi working
# WEP/WPA connection needs to be supported out-of-box
#
# Choose either 'wpa-supplicant' or 'iwd' as wireless-daemon
WIRELESS_DAEMON ??= "wpa-supplicant"
SUMMARY:packagegroup-base-wifi = "WiFi support"
RDEPENDS:packagegroup-base-wifi = "\
    iw \
    wireless-regdb-static \
    ${WIRELESS_DAEMON} \
"

RRECOMMENDS:packagegroup-base-wifi = "\
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

SUMMARY:packagegroup-base-nfc = "Near Field Communication support"
RDEPENDS:packagegroup-base-nfc = "\
    neard"

RRECOMMENDS:packagegroup-base-nfc = "\
    kernel-module-nfc"

SUMMARY:packagegroup-base-3g = "Cellular data support"
RDEPENDS:packagegroup-base-3g = "\
    ofono"

RRECOMMENDS:packagegroup-base-3g = "\
    kernel-module-cdc-acm \
    kernel-module-cdc-wdm"

SUMMARY:packagegroup-base-smbfs = "SMB network filesystem support"
RRECOMMENDS:packagegroup-base-smbfs = "\
    kernel-module-cifs \
    kernel-module-smbfs"

SUMMARY:packagegroup-base-cramfs = "cramfs filesystem support"
RRECOMMENDS:packagegroup-base-cramfs = "\
    kernel-module-cramfs"

#
# packagegroup-base-nfs provides ONLY client support - server is in nfs-utils package
#
SUMMARY:packagegroup-base-nfs = "NFS network filesystem support"
RDEPENDS:packagegroup-base-nfs = "\
    rpcbind"

RRECOMMENDS:packagegroup-base-nfs = "\
    kernel-module-nfs "

SUMMARY:packagegroup-base-zeroconf = "Zeroconf support"
RDEPENDS:packagegroup-base-zeroconf = "\
    avahi-daemon"
RDEPENDS:packagegroup-base-zeroconf:append:libc-glibc = "\
    libnss-mdns \
    "

SUMMARY:packagegroup-base-ipv6 = "IPv6 support"
RDEPENDS:packagegroup-base-ipv6 = "\
    "

RRECOMMENDS:packagegroup-base-ipv6 = "\
    kernel-module-ipv6 "

SUMMARY:packagegroup-base-serial = "Serial port support"
RDEPENDS:packagegroup-base-serial = "\
    setserial \
    lrzsz "

SUMMARY:packagegroup-base-phone = "Cellular telephony (voice) support"
RDEPENDS:packagegroup-base-phone = "\
    ofono"
