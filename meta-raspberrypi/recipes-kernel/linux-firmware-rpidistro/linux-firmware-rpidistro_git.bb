SUMMARY = "Linux kernel firmware files from Raspbian distribution"
DESCRIPTION = "Updated firmware files for RaspberryPi hardware. \
RPi-Distro obtains these directly from Cypress; they are not submitted \
to linux-firmware for general use."
HOMEPAGE = "https://github.com/RPi-Distro/firmware-nonfree"
SECTION = "kernel"

# In maintained upstream linux-firmware:
# * brcmfmac43430-sdio falls under LICENCE.cypress
# * brcmfmac43455-sdio falls under LICENCE.broadcom_bcm43xx
#
# It is likely[^1] that both of these should be under LICENCE.cypress.
# Further, at this time the text of LICENCE.broadcom_bcm43xx is the same
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
    & WHENCE \
"
LIC_FILES_CHKSUM = "\
    file://LICENCE.broadcom_bcm43xx;md5=3160c14df7228891b868060e1951dfbc \
    file://WHENCE;md5=7b12b2224438186e4c97c4c7f3a5cc28 \
"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-broadcom_bcm43xx-rpidistro] = "LICENCE.broadcom_bcm43xx"
NO_GENERIC_LICENSE[WHENCE] = "WHENCE"

SRC_URI = "git://github.com/RPi-Distro/firmware-nonfree"

SRCREV = "f0ad1a42b051aa9da1d9e1dc606dd68ec2f163a5"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit allarch

CLEANBROKEN = "1"

do_compile() {
    :
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm

    cp ./LICENCE.broadcom_bcm43xx ${D}${nonarch_base_libdir}/firmware/LICENCE.broadcom_bcm43xx-rpidistro

    # Replace outdated linux-firmware files with updated ones from
    # raspbian firmware-nonfree. Raspbian adds blobs and nvram
    # definitions that are also necessary so copy those too.
    for fw in brcmfmac43430-sdio brcmfmac43455-sdio ; do
        install -m 0644 brcm/${fw}.* ${D}${nonarch_base_libdir}/firmware/brcm/
    done
}

PACKAGES = "\
    ${PN}-broadcom-license \
    ${PN}-bcm43430 \
    ${PN}-bcm43455 \
"

LICENSE_${PN}-bcm43430 = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE_${PN}-bcm43455 = "Firmware-broadcom_bcm43xx-rpidistro"
LICENSE_${PN}-broadcom-license = "Firmware-broadcom_bcm43xx-rpidistro"
FILES_${PN}-broadcom-license = "${nonarch_base_libdir}/firmware/LICENCE.broadcom_bcm43xx-rpidistro"
FILES_${PN}-bcm43430 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43430*"
FILES_${PN}-bcm43455 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43455*"
RDEPENDS_${PN}-bcm43430 += "${PN}-broadcom-license"
RDEPENDS_${PN}-bcm43455 += "${PN}-broadcom-license"
RCONFLICTS_${PN}-bcm43430 = "\
    linux-firmware-bcm43430 \
    linux-firmware-raspbian-bcm43430 \
"
RREPLACES_${PN}-bcm43430 = "\
    linux-firmware-bcm43430 \
    linux-firmware-raspbian-bcm43430 \
"
RCONFLICTS_${PN}-bcm43455 = "\
    linux-firmware-bcm43455 \
    linux-firmware-raspbian-bcm43455 \
"
RREPLACES_${PN}-bcm43455 = "\
    linux-firmware-bcm43455 \
    linux-firmware-raspbian-bcm43455 \
"

# Firmware files are generally not run on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"
