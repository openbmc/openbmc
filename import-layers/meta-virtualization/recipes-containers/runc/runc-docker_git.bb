include runc.inc

# Note: this rev is before the required protocol field, update when all components
#       have been updated to match.
SRCREV = "2f7393a47307a16f8cee44a37b262e8b81021e3e"
SRC_URI = "git://github.com/docker/runc.git;nobranch=1 \
          "

RUNC_VERSION = "1.0.0-rc2"
PROVIDES += "virtual/runc"
RPROVIDES_${PN} = "virtual/runc"
