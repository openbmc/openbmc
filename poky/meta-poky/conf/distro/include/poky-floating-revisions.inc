#
# Set recipe versions to auto-rev for cutting edge testing
#

INHERIT += "poky-bleeding"

POKY_AUTOREV_RECIPES = "\
    libmatchbox \
    opkg-utils \
    matchbox-config-gtk \
    matchbox-desktop \
    matchbox-keyboard \
    matchbox-panel-2 \
    matchbox-terminal \
    matchbox-theme-sato \
    matchbox-wm \
    pseudo \
    puzzles \
    sato-icon-theme \
    sato-screenshot \
    settings-daemon \
"
