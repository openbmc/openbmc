EXTRANATIVEPATH += "pigz-native gzip-native"
DEPENDS += "gzip-native"

# tar may get run by do_unpack or do_populate_lic which could call gzip
do_unpack[depends] += "gzip-native:do_populate_sysroot"
