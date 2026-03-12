# Git-based Rust dependencies for ClamAV
# onenote_parser from Cisco-Talos/onenote.rs

SRC_URI += "\
    git://github.com/Cisco-Talos/onenote.rs.git;protocol=https;nobranch=1;name=onenote_parser;destsuffix=onenote_parser;type=git-dependency \
"

SRCREV_FORMAT .= "_onenote_parser"
SRCREV_onenote_parser = "8b450447e58143004b68dd21c11b710fdb79be92"
