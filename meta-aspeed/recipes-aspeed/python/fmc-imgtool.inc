SUMMARY = "ASPEED FMC tool and images"
HOMEPAGE = "https://github.com/AspeedTech-BMC/fmc_imgtool"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=522c9891674011c4a2390d828772384d"

BRANCH = "master"
SRC_URI = "git://github.com/AspeedTech-BMC/fmc_imgtool.git;protocol=https;branch=${BRANCH}"

# Tag for v00.01.02+
SRCREV = "65689e052a1854ef680ec55d42e93035c72ebaa0"

PV = "1.0+git"
S = "${WORKDIR}/git"
