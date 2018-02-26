SUMMARY = "Target packages for the Go SDK"

inherit packagegroup goarch

RDEPENDS_${PN} = " \
    go-runtime \
    go-runtime-dev \
"
