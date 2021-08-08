DEFAULTTUNE ?= "microblaze"

require conf/machine/include/microblaze/arch-microblaze.inc

AVAILTUNES += "microblaze"
TUNE_FEATURES:tune-microblaze = "microblaze"
PACKAGE_EXTRA_ARCHS:tune-microblaze = "${TUNE_PKGARCH}"

