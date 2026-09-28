inherit sbsign

SBSIGN_TARGET_BINARY = "${B}/src/boot/systemd-boot${EFI_ARCH}.efi"

do_compile:append() {
    do_sbsign
}

# Make sure the keys are actually there before trying to sign
do_compile[depends] += "gen-sbkeys:do_install"
