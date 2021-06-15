SUMMARY = "Google NCSI daemon"
DESCRIPTION = "Google NCSI daemon."
GOOGLE_MISC_PROJ = "kcsbridge"

require ../google-misc/google-misc.inc

inherit systemd

DEPENDS += " \
  fmt \
  sdbusplus \
  sdeventplus \
  stdplus \
"

SYSTEMD_SERVICE_${PN} += "kcsbridge@.service"
