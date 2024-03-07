#
# This include file is to document commonly used configuration options to minimise
# GPLv3 content in images.
#
# The intent is to allow some images/configurations to build, not everything. There
# will be a cost in reduced functionality.

# Settings for full-cmdline
RDEPENDS:packagegroup-core-full-cmdline-utils:remove = "bash bc coreutils cpio ed findutils gawk grep mc mc-shell mc-helpers mc-helpers-perl sed tar time"
RDEPENDS:packagegroup-core-full-cmdline-dev-utils:remove = "diffutils m4 make patch"
RDEPENDS:packagegroup-core-full-cmdline-multiuser:remove = "gzip"

# Settings for weston
# direct gpl3 dependencies
RRECOMMENDS:packagegroup-base-vfat:remove = "dosfstools"
PACKAGECONFIG:remove:pn-bluez5 = "readline"

# dnf pulls in gpg which is gpl3; it also pulls in python3-rpm which pulls in rpm-build which pulls in bash
# so install rpm but not dnf
IMAGE_FEATURES:remove:pn-core-image-weston = "package-management"
CORE_IMAGE_EXTRA_INSTALL:pn-core-image-weston += "rpm"
IMAGE_FEATURES:remove:pn-core-image-full-cmdline = "package-management"
CORE_IMAGE_EXTRA_INSTALL:pn-core-image-full-cmdline += "rpm"

# matchbox-terminal depends on vte, which is gpl3
CORE_IMAGE_BASE_INSTALL:remove:pn-core-image-weston = "matchbox-terminal"

# Some python-tests use bash outside of ptest
RDEPENDS:${PN}-tests:remove:class-target:pn-python3 = "${MLPREFIX}bash"
INSANE_SKIP:${PN}-tests:pn-python3 = "file-rdeps"
