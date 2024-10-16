SUMMARY = "Clear boot-once variables"
DESCRIPTION = "Clear u-boot variables used for one-time boot flow"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit obmc-phosphor-systemd

RDEPENDS:${PN} = "${@d.getVar('PREFERRED_PROVIDER_u-boot-fw-utils', True) or 'u-boot-fw-utils'}"
