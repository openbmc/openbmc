inherit obmc-phosphor-python-autotools
include sdbus++.inc

SRC_URI += "file://0001-Revert-sdbus-switch-to-python3.patch"

# Provide these aliases temporarily until everyone can move over to the
# new package name.
PROVIDES_class-native += "sdbusplus-native sdbus++-native"
PROVIDES_class-nativesdk += "sdbusplus-nativesdk sdbus++-nativesdk"
