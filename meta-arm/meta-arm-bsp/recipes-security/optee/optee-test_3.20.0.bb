require recipes-security/optee/optee-test.inc

SRC_URI += " \
    file://0001-xtest-regression_1000-remove-unneeded-stat.h-include.patch \
    file://0002-ffa_spmc-Add-arm_ffa_user-driver-compatibility-check.patch \
    file://0003-Update-arm_ffa_user-driver-dependency.patch \
   "
SRCREV = "5db8ab4c733d5b2f4afac3e9aef0a26634c4b444"
