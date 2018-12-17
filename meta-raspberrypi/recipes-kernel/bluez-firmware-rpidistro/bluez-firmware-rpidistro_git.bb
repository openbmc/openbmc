SUMMARY = "Linux kernel Bluetooth firmware files from Raspbian distribution"
DESCRIPTION = "Updated Bluetooth firmware files for RaspberryPi hardware. \
RPi-Distro obtains these directly from Cypress; they are not submitted \
to linux-firmware for general use."
HOMEPAGE = "https://github.com/RPi-Distro/bluez-firmware"
SECTION = "kernel"

# Upstream has provided[^1] licensing information in the Debian
# copyright file.  The wording of the Cypress license subsequently
# changed in linux-firmware.
#
# Rather than make assumptions about what's supposed to be what, we'll
# use the license implied by the source of these files, named to avoid
# conflicts with linux-firmware.
#
# [^1]: https://github.com/RPi-Distro/bluez-firmware/issues/1
LICENSE = "Firmware-cypress-rpidistro"
LIC_FILES_CHKSUM = "\
    file://LICENCE.cypress-rpidistro;md5=852f9d10cbedba1f6c439729bd0617b4 \
"

# These are not common licenses, set NO_GENERIC_LICENSE for them
# so that the license files will be copied from fetched source
NO_GENERIC_LICENSE[Firmware-cypress-rpidistro] = "LICENCE.cypress-rpidistro"

SRC_URI = "git://github.com/RPi-Distro/bluez-firmware"
SRCREV = "ade2bae1aaaebede09abb8fb546f767a0e4c7804"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit allarch

CLEANBROKEN = "1"

do_extract_lic() {
    # Extract the license from the Debian copyright file
    sed -e '1,23d' ${S}/debian/copyright > ${S}/LICENCE.cypress-rpidistro
}
# Must be before both do_install and do_populate_lic.  Putting it before
# their common ancestor works; other approaches do not.
addtask extract_lic after do_unpack before do_patch

do_compile() {
    :
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware/brcm

    cp LICENCE.cypress-rpidistro ${D}${nonarch_base_libdir}/firmware
    install -m 0644 broadcom/BCM434*.hcd ${D}${nonarch_base_libdir}/firmware/brcm/
}

PACKAGES = "\
    ${PN}-cypress-license \
    ${PN}-bcm43430a1-hcd \
    ${PN}-bcm4345c0-hcd \
"

LICENSE_${PN}-bcm43430a1-hcd = "Firmware-cypress-rpidistro"
LICENSE_${PN}-bcm4345c0-hcd = "Firmware-cypress-rpidistro"
LICENSE_${PN}-cypress-license = "Firmware-cypress-rpidistro"

FILES_${PN}-cypress-license = "\
    ${nonarch_base_libdir}/firmware/LICENCE.cypress-rpidistro \
"
FILES_${PN}-bcm43430a1-hcd = "\
    ${nonarch_base_libdir}/firmware/brcm/BCM43430A1.hcd \
"
FILES_${PN}-bcm4345c0-hcd = "\
    ${nonarch_base_libdir}/firmware/brcm/BCM4345C0.hcd \
"

RDEPENDS_${PN}-bcm43430a1-hcd += "${PN}-cypress-license"
RDEPENDS_${PN}-bcm4345c0-hcd += "${PN}-cypress-license"
RCONFLICTS_${PN}-bcm43430a1-hcd = "linux-firmware-bcm43430a1-hcd"
RREPLACES_${PN}-bcm43430a1-hcd = "linux-firmware-bcm43430a1-hcd"
RCONFLICTS_${PN}-bcm43435c0-hcd = "linux-firmware-bcm4345c0-hcd"
RREPLACES_${PN}-bcm43435c0-hcd = "linux-firmware-bcm4345c0-hcd"

# Firmware files are generally not run on the CPU, so they can be
# allarch despite being architecture specific
INSANE_SKIP = "arch"
