# Sign binaries for UEFI Secure Boot
#
# Usage in recipes:
#
# Set binary to sign per recipe:
# SBSIGN_TARGET_BINARY = "${B}/binary_to_sign"
#
# Then call do_sbsign() in correct stage of the build
# do_compile:append() {
#     do_sbsign
# }

DEPENDS += 'gen-sbkeys'
DEPENDS += "sbsigntool-native"

SBSIGN_KEY = "${SBSIGN_KEYS_DIR}/db.key"
SBSIGN_CERT = "${SBSIGN_KEYS_DIR}/db.crt"
SBSIGN_TARGET_BINARY ?= "binary_to_sign"

# Not adding as task since recipes may need to sign binaries at different
# stages. Instead they can call this function when needed by calling this function
do_sbsign() {
    bbnote "Signing ${PN} binary ${SBSIGN_TARGET_BINARY} with ${SBSIGN_KEY} and ${SBSIGN_CERT}"
    ${STAGING_BINDIR_NATIVE}/sbsign \
        --key "${SBSIGN_KEY}" \
        --cert "${SBSIGN_CERT}" \
        --output  "${SBSIGN_TARGET_BINARY}.signed" \
        "${SBSIGN_TARGET_BINARY}"
    cp "${SBSIGN_TARGET_BINARY}" "${SBSIGN_TARGET_BINARY}.unsigned"
    cp "${SBSIGN_TARGET_BINARY}.signed" "${SBSIGN_TARGET_BINARY}"
}
