# TC specific configuration

COMPATIBLE_MACHINE = "(tc?)"
HAFNIUM_PLATFORM = "secure_tc"

SRCREV = "4eb7b54348234d7f4bbac64bac28f683d6574ba9"
FILESEXTRAPATHS:prepend:tc := "${THISDIR}/files/tc:"

SRC_URI:remove = " \
    file://host-ld.patch \
    file://native-dtc.patch"

SRC_URI:append = " \
        file://0001-feat-emulate-cntp-timer-register-accesses-using-cnth.patch \
        file://0002-fix-ff-a-Use-FFA_INTERRUPT-to-signal-an-interrupted-.patch \
        file://0003-fix-ff-a-Add-FFA_SECONDARY_EP_REGISTER_64-to-list-of.patch \
        file://0004-feat-emulate-interrupt-controller-register-access.patch \
        file://0005-Revert-fix-ff-a-check-receiver-s-attributes-on-memor.patch \
        file://0006-tc-increase-heap-pages.patch \
        "

do_compile() {
    PATH="${S}/prebuilts/linux-x64/clang/bin:$PATH" oe_runmake -C ${S}
}
