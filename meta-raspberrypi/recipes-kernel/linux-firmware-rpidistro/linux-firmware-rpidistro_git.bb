SUMMARY = "Linux kernel firmware files from Raspbian distribution"
DESCRIPTION = "Updated firmware files for RaspberryPi hardware. \
RPi-Distro obtains these directly from Cypress; they are not submitted \
to linux-firmware for general use."
HOMEPAGE = "https://github.com/RPi-Distro/firmware-nonfree"
SECTION = "kernel"

# In maintained upstream linux-firmware:
# * brcmfmac43430-sdio falls under LICENSE.cypress
# * brcmfmac43455-sdio falls under LICENSE.broadcom_bcm43xx
# * brcmfmac43456-sdio falls under LICENSE.broadcom_bcm43xx
#
# It is likely[^1] that both of these should be under LICENSE.cypress.
# Further, at this time the text of LICENSE.broadcom_bcm43xx is the same
# in linux-firmware and RPi-Distro/firmware-nonfree, but this may
# change.
#
# Rather than make assumptions about what's supposed to be what, we'll
# use the license implied by the source of these files, named to avoid
# conflicts with linux-firmware.
#
# [^1]: https://github.com/RPi-Distro/bluez-firmware/issues/1
LICENSE = "\
    Firmware-broadcom_bcm43xx-rpidistro \
"
LIC_FILES_CHKSUM = "\
    file://debian/config/brcm80211/LICENSE;md5=8cba1397cda6386db37210439a0da3eb \
"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-broadcom_bcm43xx-rpidistro] = "debian/config/brcm80211/LICENSE"

SRC_URI = "git://github.com/RPi-Distro/firmware-nonfree;branch=bullseye;protocol=https"

SRCREV = "b3eec612566ca08913f0830d299f4df70297428f"
PV = "20210315-3+rpt3"

S = "${WORKDIR}/git"

inherit allarch

CLEANBROKEN = "1"

do_compile() {
    :
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm ${D}${nonarch_base_libdir}/firmware/cypress

    cp debian/config/brcm80211/LICENSE ${D}${nonarch_base_libdir}/firmware/LICENSE.broadcom_bcm43xx-rpidistro

    # Replace outdated linux-firmware files with updated ones from
    # raspbian firmware-nonfree. Raspbian adds blobs and nvram
    # definitions that are also necessary so copy those too.
    for fw in brcmfmac43430-sdio brcmfmac43436-sdio brcmfmac43436s-sdio brcmfmac43455-sdio brcmfmac43456-sdio ; do
        cp -R --no-dereference --preserve=mode,links -v debian/config/brcm80211/brcm/${fw}.* ${D}${nonarch_base_libdir}/firmware/brcm/
    done
    cp -R --no-dereference --preserve=mode,links -v debian/config/brcm80211/cypress/* ${D}${nonarch_base_libdir}/firmware/cypress/
    # add compat links. Fixes errors like
    # brcmfmac mmc1:0001:1: Direct firmware load for brcm/brcmfmac43455-sdio.raspberrypi,4-model-compute-module.txt failed with error -2
    ln -s brcmfmac43455-sdio.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.raspberrypi,4-compute-module.txt
}

PACKAGES = "\
    ${PN}-broadcom-license \
    ${PN}-bcm43430 \
    ${PN}-bcm43455 \
    ${PN}-bcm43456 \
    ${PN}-bcm43436 \
    ${PN}-bcm43436s \
"

LICENSE:${PN}-bcm43430 = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE:${PN}-bcm43436 = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE:${PN}-bcm43436s = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE:${PN}-bcm43455 = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE:${PN}-bcm43456 = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE:${PN}-broadcom-license = "Firmware-broadcom_bcm43xx-rpidistro"
FILES:${PN}-broadcom-license = "${nonarch_base_libdir}/firmware/LICENSE.broadcom_bcm43xx-rpidistro"
FILES:${PN}-bcm43430 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43430* ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.bin"
FILES:${PN}-bcm43436 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43436-*"
FILES:${PN}-bcm43436s = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43436s*"
FILES:${PN}-bcm43455 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43455* ${nonarch_base_libdir}/firmware/cypress/cyfmac43455-sdio.*"
FILES:${PN}-bcm43456 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43456*"
RDEPENDS:${PN}-bcm43430 += "${PN}-broadcom-license"
RDEPENDS:${PN}-bcm43436 += "${PN}-broadcom-license"
RDEPENDS:${PN}-bcm43436s += "${PN}-broadcom-license"
RDEPENDS:${PN}-bcm43455 += "${PN}-broadcom-license"
RDEPENDS:${PN}-bcm43456 += "${PN}-broadcom-license"
RCONFLICTS:${PN}-bcm43430 = "\
    linux-firmware-bcm43430 \
    linux-firmware-raspbian-bcm43430 \
"

RREPLACES:${PN}-bcm43430 = "\
    linux-firmware-bcm43430 \
    linux-firmware-raspbian-bcm43430 \
"

RCONFLICTS:${PN}-bcm43436 = "\
    linux-firmware-bcm43436 \
    linux-firmware-raspbian-bcm43436 \
"

RREPLACES:${PN}-bcm43436 = "\
    linux-firmware-bcm43436 \
    linux-firmware-raspbian-bcm43436 \
"

RCONFLICTS:${PN}-bcm43436s = "\
    linux-firmware-bcm43436s \
    linux-firmware-raspbian-bcm43436s \
"

RREPLACES:${PN}-bcm43436s = "\
    linux-firmware-bcm43436s \
    linux-firmware-raspbian-bcm43436s \
"

RCONFLICTS:${PN}-bcm43455 = "\
    linux-firmware-bcm43455 \
    linux-firmware-raspbian-bcm43455 \
"
RREPLACES:${PN}-bcm43455 = "\
    linux-firmware-bcm43455 \
    linux-firmware-raspbian-bcm43455 \
"
RCONFLICTS:${PN}-bcm43456 = "\
    linux-firmware-bcm43456 \
    linux-firmware-raspbian-bcm43456 \
"
RREPLACES:${PN}-bcm43456 = "\
    linux-firmware-bcm43456 \
    linux-firmware-raspbian-bcm43456 \
"

# Firmware files are generally not run on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"
