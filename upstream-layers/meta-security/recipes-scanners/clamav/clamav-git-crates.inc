# Git-based Rust dependencies for ClamAV
# onenote_parser from Cisco-Talos/onenote.rs
# clam-sigutil from Cisco-Talos/clamav-signature-util

SRC_URI += "\
    git://github.com/Cisco-Talos/onenote.rs.git;protocol=https;nobranch=1;name=onenote_parser;destsuffix=onenote_parser;type=git-dependency \
    git://github.com/Cisco-Talos/clamav-signature-util.git;protocol=https;nobranch=1;name=clam-sigutil;destsuffix=clam-sigutil;type=git-dependency \
"

SRCREV_FORMAT .= "_onenote_parser"
SRCREV_onenote_parser = "29c08532252b917543ff268284f926f30876bb79"

SRCREV_FORMAT .= "_clam-sigutil"
SRCREV_clam-sigutil = "7cfeb7f630ce472239f5b0a794b62df7f592acc7"
