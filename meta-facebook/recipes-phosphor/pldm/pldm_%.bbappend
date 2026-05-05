PACKAGECONFIG:append = " oem-meta"
PACKAGECONFIG:remove = "softoff"

EXTRA_OEMESON:append = " \
  -Dfw-update-pkg-inotify=enabled \
"
