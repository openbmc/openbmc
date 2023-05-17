## Some packages do not allow a simple bbappend to tweak the PACKAGECONFIG
## because they can end up inheriting bbclasses, which cause other
## side-effects before the bbappend is applied.  Place tweaks for those
## packages in this file.

# Disable python from btrfs-tools
PACKAGECONFIG:remove:pn-btrfs-tools = "python"
