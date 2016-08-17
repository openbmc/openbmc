TOOLCHAIN_TARGET_EFLTASK ?= "packagegroup-efl-standalone-sdk-target packagegroup-efl-standalone-sdk-target-dbg"
TOOLCHAIN_TARGET_TASK = "${TOOLCHAIN_TARGET_EFLTASK}"
TOOLCHAIN_OUTPUTNAME = "${SDK_NAME}-toolchain-efl-${DISTRO_VERSION}"
require recipes-core/meta/meta-toolchain.bb

TOOLCHAIN_NEED_CONFIGSITE_CACHE += "zlib"
