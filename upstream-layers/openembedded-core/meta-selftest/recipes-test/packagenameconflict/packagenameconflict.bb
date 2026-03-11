SUMMARY = "Test case that tries to rename a package to an existing one and fails"
DESCRIPTION = "This generates a packaging error when a package is renamed to a pre-existing name"
LICENSE = "MIT"

# Add a new package ${PN}-renametest
PACKAGES += "${PN}-renametest"
# ... and try to rename the ${PN}-dev to the new ${PN}-renametest (conflict)
PKG:${PN}-dev = "${PN}-renametest"

EXCLUDE_FROM_WORLD = "1"
