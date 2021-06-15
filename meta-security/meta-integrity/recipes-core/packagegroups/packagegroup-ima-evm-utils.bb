SUMMARY = "IMA/EVM userspace tools"
LICENSE = "MIT"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "ima"

# Only one at the moment, but perhaps more will come in the future.
RDEPENDS_${PN} = " \
    ima-evm-utils \
"
