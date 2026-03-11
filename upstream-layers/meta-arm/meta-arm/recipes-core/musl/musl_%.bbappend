FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "${@bb.utils.contains('MACHINE_FEATURES', 'ts-crypto', 'file://0001-Revert-ldso-fix-non-functional-fix-to-early-dynamic-.patch', '', d)}"
