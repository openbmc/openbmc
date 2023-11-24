# TC specific configuration

COMPATIBLE_MACHINE = "(tc?)"
HAFNIUM_PLATFORM = "secure_tc"

do_compile() {
    PATH="${S}/prebuilts/linux-x64/clang/bin:$PATH" oe_runmake -C ${S}
}
