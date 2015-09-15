require ${BPN}.inc

SRC_URI += "file://0001-Use-proc-self-exe-for-swig-swiglib-on-non-Win32-plat.patch \
            file://0001-configure-use-pkg-config-for-pcre-detection.patch \
           "
SRC_URI[md5sum] = "df43ae271642bcfa61c1e59f970f9963"
SRC_URI[sha256sum] = "c67f63ea11956106e4cda66416d5020330dc4ce2ee45057d39a9494ce33eca05"

