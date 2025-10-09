SUMMARY = "OpenBMC TPM Package Group"
PR = "r1"

# Since tpm2 requires libtss2-tcti-device, we need to change the PACKAGE_ARCH
# from 'allarch' to avoid a warning:
#   ERROR: packagegroup-openbmc-tpm-1.0-r1 do_package_write_ipk: An allarch
#   packagegroup shouldn't depend on packages which are dynamically renamed
#   (libtss2-tcti-device to libtss2-tcti-device0)
#
# Per https://github.com/openembedded/openembedded-core/commit/5bf3e447d2f5064495d83a8fad30229bcf1ecc9b
# this must be done before the `inherit packagegroup`.
PACKAGE_ARCH:mf-tpm2 = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS:${PN}:append:mf-tpm1 = " tpm-tools trousers"
RDEPENDS:${PN}:append:mf-tpm2 = " tpm2-tools libtss2-tcti-device"
