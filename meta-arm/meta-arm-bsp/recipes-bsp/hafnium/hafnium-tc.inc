# TC specific configuration

COMPATIBLE_MACHINE = "(tc?)"
HAFNIUM_PLATFORM = "secure_tc"

FILESEXTRAPATHS:prepend:tc := "${THISDIR}/files/tc:"

SRC_URI:append = " \
        file://0001-feat-emulate-cntp-timer-register-accesses-using-cnth.patch \
        file://0002-feat-emulate-interrupt-controller-register-access.patch \
        file://0003-feat-vhe-set-STAGE1_NS-while-mapping-memory-from-NWd.patch \
        file://0001-feat-vhe-enable-vhe-and-disable-branch-protection-fo.patch;patchdir=project/reference \
        "

do_compile() {
    PATH="${S}/prebuilts/linux-x64/clang/bin:$PATH" oe_runmake -C ${S}
}
