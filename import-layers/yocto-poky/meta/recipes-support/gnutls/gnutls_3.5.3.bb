require gnutls.inc

SRC_URI += "file://correct_rpl_gettimeofday_signature.patch \
            file://0001-configure.ac-fix-sed-command.patch \
            file://use-pkg-config-to-locate-zlib.patch \
            file://0001-Use-correct-include-dir-with-minitasn.patch \
            file://CVE-2016-7444.patch \
           "
SRC_URI[md5sum] = "6c2c7f40ddf52933ee3ca474cb8cb63c"
SRC_URI[sha256sum] = "92c4bc999a10a1b95299ebefaeea8333f19d8a98d957a35b5eae74881bdb1fef"

# x86 .text relocations should be fixed from 3.5.5 onwards
INSANE_SKIP_${PN}_append_x86 = " textrel"
