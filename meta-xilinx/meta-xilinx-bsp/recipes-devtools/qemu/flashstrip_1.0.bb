SUMMARY = "Building and installing flash strip utility"
DESCRIPTION = "Building and installing flash strip utility"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../flash_stripe.c;beginline=1;endline=23;md5=abb859d98b7c4eede655e1b71824125a"

B = "${WORKDIR}/build"

SRC_URI += "file://flash_stripe.c"

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile() {
    ${CC} ${WORKDIR}/flash_stripe.c -o flash_strip
    ${CC} ${WORKDIR}/flash_stripe.c -o flash_unstrip
    ${CC} ${WORKDIR}/flash_stripe.c -o flash_strip_bw -DFLASH_STRIPE_BW
    ${CC} ${WORKDIR}/flash_stripe.c -o flash_unstrip_bw -DUNSTRIP -DFLASH_STRIPE_BW
}

do_install() {
    install -d ${D}${bindir}
    install -Dm 0755 ${B}/* ${D}${bindir}/
}

FILES_${PN} = "${bindir}/*"
