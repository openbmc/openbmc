DEPENDS_append = " go-dep-native"

do_compile_prepend() {
    rm -f ${WORKDIR}/build/src/${GO_IMPORT}/Gopkg.toml
    rm -f ${WORKDIR}/build/src/${GO_IMPORT}/Gopkg.lock
    ( cd ${WORKDIR}/build/src/${GO_IMPORT} && dep init && dep ensure )
}

