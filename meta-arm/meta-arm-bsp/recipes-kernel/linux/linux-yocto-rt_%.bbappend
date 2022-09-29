# Only enable linux-yocto-rt for n1sdp and the Armv8-R AArch64 AEM FVP
LINUX_YOCTO_RT_REQUIRE ?= ""
LINUX_YOCTO_RT_REQUIRE:n1sdp = "linux-arm-platforms.inc"
LINUX_YOCTO_RT_REQUIRE:fvp-baser-aemv8r64 = "linux-arm-platforms.inc"

require ${LINUX_YOCTO_RT_REQUIRE}
