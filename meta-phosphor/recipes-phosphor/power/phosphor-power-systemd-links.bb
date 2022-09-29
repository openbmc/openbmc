# TODO: This is splitted into two recipes;
#       To avoid build error, this is kept for now.
#       Remove me when the refactor of phosphor-power recipe is finished
SUMMARY = "Phosphor Power services installation"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

inherit allarch

RDEPENDS:${PN} += "phosphor-power"

ALLOW_EMPTY:${PN} = "1"
