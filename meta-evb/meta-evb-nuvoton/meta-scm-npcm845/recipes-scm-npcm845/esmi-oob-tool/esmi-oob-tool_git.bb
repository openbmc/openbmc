DESCRIPTION = "esmi oob tool"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://github.com/amd/esmi_oob_library.git;branch=master;protocol=https \
        file://0001-fixed-header-incude.patch \
    "

SRCREV = "00cc0fb0265af1d240a0aff5ed96f90a73ff8c51"

S = "${WORKDIR}/git"

inherit cmake 

