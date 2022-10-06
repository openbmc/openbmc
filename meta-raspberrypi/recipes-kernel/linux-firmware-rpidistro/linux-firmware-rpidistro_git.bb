SUMMARY = "Linux kernel firmware files from Raspbian distribution"
DESCRIPTION = "Updated firmware files for RaspberryPi hardware. \
RPi-Distro obtains these directly from Cypress; they are not submitted \
to linux-firmware for general use."
HOMEPAGE = "https://github.com/RPi-Distro/firmware-nonfree"
SECTION = "kernel"

LICENSE = "GPL-2.0-only & binary-redist-Cypress-rpidistro & Synaptics-rpidistro"
LIC_FILES_CHKSUM = "\
    file://debian/config/brcm80211/copyright;md5=b0630b02d90e3da72206c909b6aecc8c \
"
# Where these are no common licenses, set NO_GENERIC_LICENSE so that the
# license files will be copied from the fetched source.
NO_GENERIC_LICENSE[binary-redist-Cypress-rpidistro] = "debian/config/brcm80211/copyright"
NO_GENERIC_LICENSE[Synaptics-rpidistro] = "debian/config/brcm80211/copyright"
LICENSE_FLAGS = "synaptics-killswitch"

SRC_URI = "git://github.com/RPi-Distro/firmware-nonfree;branch=bullseye;protocol=https \
    file://0001-Default-43455-firmware-to-standard-variant.patch \
"
SRCREV = "541e5a05d152e7e6f0d9be45622e4a3741e51c02"
PV = "20210315-3+rpt7"
S = "${WORKDIR}/git"

inherit allarch

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm ${D}${nonarch_base_libdir}/firmware/cypress

    cp debian/config/brcm80211/copyright ${D}${nonarch_base_libdir}/firmware/copyright.firmware-nonfree-rpidistro

    for fw in \
            brcmfmac43430-sdio \
            brcmfmac43436-sdio \
            brcmfmac43436s-sdio \
            brcmfmac43455-sdio \
            brcmfmac43456-sdio; do
        cp -R --no-dereference --preserve=mode,links -v debian/config/brcm80211/brcm/${fw}.* ${D}${nonarch_base_libdir}/firmware/brcm/
    done

    cp -R --no-dereference --preserve=mode,links -v debian/config/brcm80211/cypress/* ${D}${nonarch_base_libdir}/firmware/cypress/

    rm ${D}${nonarch_base_libdir}/firmware/cypress/README.txt

    # add compat links. Fixes errors like
    # brcmfmac mmc1:0001:1: Direct firmware load for brcm/brcmfmac43455-sdio.raspberrypi,4-model-compute-module.txt failed with error -2
    ln -s brcmfmac43455-sdio.txt ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.raspberrypi,4-compute-module.txt
    # brcmfmac mmc1:0001:1: Direct firmware load for brcm/brcmfmac43455-sdio.raspberrypi,4-model-b.bin failed with error -2
    ln -s brcmfmac43455-sdio.bin ${D}${nonarch_base_libdir}/firmware/brcm/brcmfmac43455-sdio.raspberrypi,4-model-b.bin
}

PACKAGES = "\
    ${PN}-bcm43430 \
    ${PN}-bcm43436 \
    ${PN}-bcm43436s \
    ${PN}-bcm43455 \
    ${PN}-bcm43456 \
    ${PN}-license \
"

LICENSE:${PN}-bcm43430 = "binary-redist-Cypress-rpidistro"
LICENSE:${PN}-bcm43436 = "Synaptics-rpidistro"
LICENSE:${PN}-bcm43436s = "Synaptics-rpidistro"
LICENSE:${PN}-bcm43455 = "binary-redist-Cypress-rpidistro"
LICENSE:${PN}-bcm43456 = "Synaptics-rpidistro"
LICENSE:${PN}-license = "GPL-2.0-only"

FILES:${PN}-bcm43430 = " \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43430* \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.bin \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43430-sdio.clm_blob \
"
FILES:${PN}-bcm43436 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43436-*"
FILES:${PN}-bcm43436s = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43436s*"
FILES:${PN}-bcm43455 = " \
    ${nonarch_base_libdir}/firmware/brcm/brcmfmac43455* \
    ${nonarch_base_libdir}/firmware/cypress/cyfmac43455-sdio* \
"
FILES:${PN}-bcm43456 = "${nonarch_base_libdir}/firmware/brcm/brcmfmac43456*"
FILES:${PN}-license = "${nonarch_base_libdir}/firmware/copyright.firmware-nonfree-rpidistro"

RDEPENDS:${PN}-bcm43430 += "${PN}-license"
RDEPENDS:${PN}-bcm43436 += "${PN}-license"
RDEPENDS:${PN}-bcm43436s += "${PN}-license"
RDEPENDS:${PN}-bcm43455 += "${PN}-license"
RDEPENDS:${PN}-bcm43456 += "${PN}-license"

RCONFLICTS:${PN}-bcm43430 = "linux-firmware-raspbian-bcm43430"
RCONFLICTS:${PN}-bcm43436 = "linux-firmware-bcm43436"
RCONFLICTS:${PN}-bcm43436s = "linux-firmware-bcm43436s"
RCONFLICTS:${PN}-bcm43455 = "linux-firmware-bcm43455"
RCONFLICTS:${PN}-bcm43456 = "linux-firmware-bcm43456"

RREPLACES:${PN}-bcm43430 = "linux-firmware-bcm43430"
RREPLACES:${PN}-bcm43436 = "linux-firmware-bcm43436"
RREPLACES:${PN}-bcm43436s = "linux-firmware-bcm43436s"
RREPLACES:${PN}-bcm43455 = "linux-firmware-bcm43455"
RREPLACES:${PN}-bcm43456 = "linux-firmware-bcm43456"

# Firmware files are generally not run on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"
