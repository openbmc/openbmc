SUMMARY = "IMA/EVM userspace tools"
LICENSE = "MIT"

inherit packagegroup

# Only one at the moment, but perhaps more will come in the future.
RDEPENDS_${PN} = " \
    ima-evm-utils \
"
