LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

ALLOW_EMPTY_${PN} = "1"

PROVIDES += "virtual/obmc-fan-mgmt"
RPROVIDES:${PN} += "virtual-obmc-fan-mgmt"

