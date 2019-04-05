require xorg-app-common.inc

SUMMARY = "Utility for modifying keymaps and pointer button mappings in X"

DESCRIPTION = "The xmodmap program is used to edit and display the \
keyboard modifier map and keymap table that are used by client \
applications to convert event keycodes into keysyms. It is usually run \
from the user's session startup script to configure the keyboard \
according to personal tastes."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=272c17e96370e1e74773fa22d9989621"

PE = "1"
SRC_URI[md5sum] = "51f1d30a525e9903280ffeea2744b1f6"
SRC_URI[sha256sum] = "473f0941d7439d501bb895ff358832b936ec34c749b9704c37a15e11c318487c"
