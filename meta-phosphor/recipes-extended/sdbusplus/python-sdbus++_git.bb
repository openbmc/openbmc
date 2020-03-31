inherit obmc-phosphor-python-autotools
include sdbus++.inc

# Provide these aliases temporarily until everyone can move over to the
# new package name.
PROVIDES_class-native += "sdbusplus-native sdbus++-native"
PROVIDES_class-nativesdk += "sdbusplus-nativesdk sdbus++-nativesdk"
