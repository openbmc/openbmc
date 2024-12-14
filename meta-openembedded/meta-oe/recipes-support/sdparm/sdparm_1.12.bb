SUMMARY = "fetch and change SCSI mode pages"
DESCRIPTION = "The sdparm utility accesses and optionally modifies \
SCSI devices' mode page and inquiry data."
HOMEPAGE = "http://sg.danny.cz/sg/sdparm.html"
SECTION = "console/utils"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecab6c36b7ba82c675581dd0afde36f7 \
                    file://lib/BSD_LICENSE;md5=12cde17a04c30dece2752f36b7192c64"
DEPENDS="sg3-utils"
SRC_URI = "http://sg.danny.cz/sg/p/${BPN}-${PV}.tgz \
           file://make-sysroot-work.patch \
           "
MIRRORS += "http://sg.danny.cz/sg/p https://fossies.org/linux/misc"

UPSTREAM_CHECK_REGEX = "sdparm-(?P<pver>\d+(\.\d+)+)\.tgz"

PACKAGES =+ "${PN}-scripts"
RDEPENDS:${PN}-scripts += "bash ${PN}"

SRC_URI[sha256sum] = "e7f84247069da9a0c293963948d8aba8e5897a13e35e5476c8258acb7ca3a124"

inherit autotools

# Put the bash scripts to ${PN}-scripts
FILES:${PN}-scripts = "${bindir}/sas_disk_blink ${bindir}/scsi_ch_swp"
