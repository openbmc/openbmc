SUMMARY = "Target packages for the Go SDK"

inherit packagegroup goarch

RDEPENDS:${PN} = " \
    go \
    go-runtime \
    go-runtime-dev \
    go-runtime-staticdev \
"

COMPATIBLE_HOST = "^(?!riscv32).*"
