# ventura2a7 uses Ventura2_SCM_2700 as the SCM board inventory
do_install:prepend() {
    sed -i 's|/board/Ventura2_SCM"|/board/Ventura2_SCM_2700"|' \
        ${UNPACKDIR}/ventura2_*.json
}
