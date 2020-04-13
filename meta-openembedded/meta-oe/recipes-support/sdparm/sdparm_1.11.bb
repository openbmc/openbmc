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
RDEPENDS_${PN}-scripts += "bash ${PN}"

SRC_URI[md5sum] = "cd998d1c12a4ec11652d0af580f06b4d"
SRC_URI[sha256sum] = "432fdbfe90f0c51640291faf7602489b0ae56dfb96d0c02ed02308792adc7fb0"

inherit autotools

# Put the bash scripts to ${PN}-scripts
FILES_${PN}-scripts = "${bindir}/sas_disk_blink ${bindir}/scsi_ch_swp"
