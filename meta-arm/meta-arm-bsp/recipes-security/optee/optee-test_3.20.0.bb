require recipes-security/optee/optee-test.inc

SRC_URI += " \
    file://Update-arm_ffa_user-driver-dependency.patch \
    file://ffa_spmc-Add-arm_ffa_user-driver-compatibility-check.patch \
    file://musl-workaround.patch \
   "
SRCREV = "5db8ab4c733d5b2f4afac3e9aef0a26634c4b444"

EXTRA_OEMAKE:append = " OPTEE_OPENSSL_EXPORT=${STAGING_INCDIR}"
DEPENDS:append = " openssl"
CFLAGS:append = " -Wno-error=deprecated-declarations"
