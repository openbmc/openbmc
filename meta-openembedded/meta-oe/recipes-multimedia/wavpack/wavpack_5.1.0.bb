DESCRIPTION = "WavPack is a completely open audio compression format providing lossless, high-quality lossy, and a unique hybrid compression mode."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=8c1a910e5c2a8b30ec8ffb2ffa63d9b2"

SRC_URI = "git://github.com/dbry/WavPack.git;branch=master;protocol=https"
SRCREV = "9ccc3fe4a37d069137ceabe513a4dd9b0a09c1c2"
S = "${WORKDIR}/git"

inherit autotools lib_package
