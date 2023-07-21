SRC_URI += "\
    git://github.com/zowens/crc32c;protocol=https;nobranch=1;name=crc32c;destsuffix=crc32c;type=git-dependency \
    git://github.com/jthornber/rio;protocol=https;nobranch=1;name=rio;destsuffix=rio;type=git-dependency \
"

SRCREV_FORMAT .= "_crc32c"
SRCREV_crc32c = "3779fe88fea034922f808586b2564fbcab31efdc"

SRCREV_FORMAT .= "_rio"
SRCREV_rio = "2979a720f671e836302c01546f9cc9f7988610c8"
