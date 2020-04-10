inherit obmc-phosphor-python-autotools
include sdbus++.inc

SRC_URI += "file://0001-Revert-sdbus-switch-to-python3.patch"

# Provide these aliases temporarily until everyone can move over to the
# new package name.
PROVIDES_class-native += "sdbusplus-native sdbus++-native"
PROVIDES_class-nativesdk += "sdbusplus-nativesdk sdbus++-nativesdk"

# If anyone wanted the sdbus++ executable, make them use the python3 version.
# This avoids issues where both packages attempt to install the executable
# into /usr/bin.
DEPENDS += "python3-sdbus++-native"
do_install_append() {
    rm ${D}${bindir}/sdbus++
    rmdir ${D}${bindir} || true
}
