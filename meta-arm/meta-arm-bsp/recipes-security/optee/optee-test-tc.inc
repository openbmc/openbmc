# TC specific configuration

FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-test/tc:"
SRC_URI:append:tc = " \
    file://0001-xtest-Limit-tests-to-a-single-thread.patch \
    "

COMPATIBLE_MACHINE = "(tc?)"
