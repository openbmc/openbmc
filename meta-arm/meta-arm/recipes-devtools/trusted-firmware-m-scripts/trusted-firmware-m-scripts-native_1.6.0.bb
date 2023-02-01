
SRC_URI_TRUSTED_FIRMWARE_M ?= "git://git.trustedfirmware.org/TF-M/trusted-firmware-m.git;protocol=https"
SRC_URI = "${SRC_URI_TRUSTED_FIRMWARE_M};branch=${SRCBRANCH}"
# Use the wrapper script from TF-Mv1.6.0
SRCBRANCH ?= "release/1.6.x"
SRCREV = "7387d88158701a3c51ad51c90a05326ee12847a8"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=07f368487da347f3c7bd0fc3085f3afa"

S = "${WORKDIR}/git"

inherit native

RDEPENDS:${PN} = "python3-imgtool-native python3-click-native"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}/${libdir}
    cp -rf ${S}/bl2/ext/mcuboot/scripts/ ${D}/${libdir}/tfm-scripts
    cp -rf ${S}/bl2/ext/mcuboot/*.pem ${D}/${libdir}/tfm-scripts
}
FILES:${PN} = "${libdir}/tfm-scripts"
