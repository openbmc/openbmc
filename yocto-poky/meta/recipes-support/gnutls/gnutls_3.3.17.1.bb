require gnutls.inc

SRC_URI += "file://correct_rpl_gettimeofday_signature.patch \
            file://configure.ac-fix-sed-command.patch \
            file://use-pkg-config-to-locate-zlib.patch \
           "
SRC_URI[md5sum] = "8d01c7e7f2cbc5871fdca832d2260b6b"
SRC_URI[sha256sum] = "b40f158030a92f450a07b20300a3996710ca19800848d9f6fd62493170c5bbb4"
