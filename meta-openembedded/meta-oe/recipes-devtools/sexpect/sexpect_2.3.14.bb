SUMMARY = "sexpect is another implementation of Expect which is specifically designed for Shell scripts"
HOMEPAGE = "https://github.com/clarkwang/sexpect"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://github.com/clarkwang/sexpect;branch=${BRANCH};protocol=https"
SRCREV = "532a52d36aae442b7fe1ce20a59effd1dbc6e6fe"

S = "${WORKDIR}/git"

inherit cmake
