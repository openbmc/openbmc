RDEPENDS:packagegroup-meta-oe-graphics:append = "${@bb.utils.contains('BBFILE_COLLECTIONS', 'multimedia-layer', ' taisei', '', d)}"
