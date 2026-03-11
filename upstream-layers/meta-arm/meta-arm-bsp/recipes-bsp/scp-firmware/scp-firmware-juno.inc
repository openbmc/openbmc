# juno specific SCP configuration

COMPATIBLE_MACHINE = "juno"

FW_TARGETS = "scp"
FW_INSTALL:append = " romfw_bypass"

do_install:append() {
    for TYPE in ${FW_INSTALL}; do
        if [ "$TYPE" = "romfw_bypass" ]; then
            install -D "${B}/${TYPE}/${FW_TARGETS}/bin/${SCP_PLATFORM}-bl1-bypass.bin" "${D}/firmware/${FW}_${TYPE}.bin"
            install -D "${B}/${TYPE}/${FW_TARGETS}/bin/${SCP_PLATFORM}-bl1-bypass.elf" "${D}/firmware/${FW}_${TYPE}.elf"
        fi
    done
}
