# Backport a fix that is only in main, otherwise all received RAs will fail
# to be parsed.
SRC_URI += "file://0001-socket-util-introduce-CMSG_SPACE_TIMEVAL-TIMESPEC-ma.patch"

# Pin to v249.4 to fix systemd-networkd segfaults.
SRCREV = "4d8fd88b9641fce81272f60f556543f713175403"

# Additional unsubmitted PR #20752 required to fix additional systemd-networkd
# segfaults.
SRC_URI += "file://0001-journal-network-timesync-fix-segfault-on-32bit-timev.patch"
