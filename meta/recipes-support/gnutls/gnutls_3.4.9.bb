require gnutls.inc

SRC_URI += "file://correct_rpl_gettimeofday_signature.patch \
            file://0001-configure.ac-fix-sed-command.patch \
            file://use-pkg-config-to-locate-zlib.patch \
           "
SRC_URI[md5sum] = "1b3b6d55d0e2b6d01a54f53129f1da9b"
SRC_URI[sha256sum] = "48594fadba33d450f796ec69526cf2bce6ff9bc3dc90fbd7bf38dc3601f57c3f"
