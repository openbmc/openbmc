require xorg-app-common.inc

SUMMARY = "Utility for modifying keymaps and pointer button mappings in X"

DESCRIPTION = "The xmodmap program is used to edit and display the \
keyboard modifier map and keymap table that are used by client \
applications to convert event keycodes into keysyms. It is usually run \
from the user's session startup script to configure the keyboard \
according to personal tastes."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=76caa89655fe179dbb374831c98ca65f"

PE = "1"
SRC_URI[sha256sum] = "fc54b9b5bbf2ae58ba8f9d42bd051c41c7438377400c42c17d7496d19e1bb3ce"

SRC_URI_EXT = "xz"

XORGBUILDCLASS = "meson"
