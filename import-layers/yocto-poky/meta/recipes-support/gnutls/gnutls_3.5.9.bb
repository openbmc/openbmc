require gnutls.inc

SRC_URI += "file://correct_rpl_gettimeofday_signature.patch \
            file://0001-configure.ac-fix-sed-command.patch \
            file://use-pkg-config-to-locate-zlib.patch \
            file://arm_eabi.patch \
           "
SRC_URI[md5sum] = "0ab25eb6a1509345dd085bc21a387951"
SRC_URI[sha256sum] = "82b10f0c4ef18f4e64ad8cef5dbaf14be732f5095a41cf366b4ecb4050382951"

